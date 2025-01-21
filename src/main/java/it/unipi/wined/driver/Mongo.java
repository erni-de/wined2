package it.unipi.wined.driver;

//Import relative per usare MongoDB
import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BucketOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Aggregates.bucket;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Accumulators.sum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

//Import delle classi del nostro progetto
import it.unipi.wined.bean.*;
import it.unipi.wined.config.Driver_Config;

//Import di Jackson
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 *
 * @author nicol
 */

public class Mongo {

    private static MongoClient myClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> collection;

    private static final ConnectionString uri =
            new ConnectionString(Driver_Config.getMongoConnectionString());

    private static void openConnection(String collectionName){
        try{
            myClient = MongoClients.create(uri);
            db = myClient.getDatabase(Driver_Config.getMongoDbName());
            collection = db.getCollection(collectionName);
            System.out.println("Connection aperta correttamente a " + collectionName);
        }catch(Exception e){
            System.out.println("Errore nell'aprire la collection " + collection);
            e.printStackTrace();
        }
    }

    private static void closeConnection(){
        if(myClient != null){
            try{
                myClient.close();
                System.out.println("Connessione chiusa correttamente");
            }catch(Exception e){
                System.out.println("Errore nel chiudere la connessione");
                e.printStackTrace();
            }
        }
    }

    //+----------------------CRUD OPERATIONS INTO THE USER --------------------+
    public static boolean addUser(User user){
        openConnection("Users");
        try{
            Document ext_User = collection.find(eq("nickname", user.getNickname())).first();
            if (ext_User != null){
                System.out.println("Un utente è già registrato con questo nickname,"
                        + "scegline un'altro");
                closeConnection();
                return false;
            }

            Document doc = new Document();
            doc.append("firstname", user.getFirstName());
            doc.append("lastname", user.getLastName());
            doc.append("email", user.getEmail());
            doc.append("phone", user.getPhone());
            doc.append("birthday", user.getBirthday());
            doc.append("gender", user.getGender());
            doc.append("address", user.getAddress());
            doc.append("nickname", user.getNickname());
            doc.append("password", user.getPassword());

            if(user.getUser_level() != null){
                doc.append("user_level", user.getUser_level().toString());
            }

            if(user.getPayment() != null){
                Document paymentDoc = new Document();
                paymentDoc.append("card_number", user.getPayment().getCardNumber());
                paymentDoc.append("CVV", user.getPayment().getCVV());
                paymentDoc.append("expire_date", user.getPayment().getExpirationDate());
                doc.append("payment", paymentDoc);
            }

            doc.append("orders", new ArrayList<Document>());

            collection.insertOne(doc);

            System.out.println("L'utente è stato aggiunto correttamente !");
        }catch(Exception e){
            System.out.println("Errore nel creare l'utente");
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static User RetrieveUser(String nickname){
        openConnection("Users");
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        try{
            Document result = collection.find(eq("nickname",nickname)).first();
            if(result != null){
                User result_user = deserialize.readValue(result.toJson(), User.class);
                closeConnection();
                return result_user;
            }else{
                System.out.println("Nessun utente presente con quel nickname");
                return null;
            }
        }catch(Exception e){
            System.out.println("Errore improvviso nella lettura dal database");
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    public static User.Level RetrieveUserLevel(String nickname, String password){
        openConnection("Users");
        try {
            Document userDoc = collection.find(eq("nickname", nickname)).first();
            if (userDoc == null){
                System.out.println("Retrieve fallito: nickname non trovato");
                closeConnection();
                return null;
            }
            String storedPassword = userDoc.getString("password");
            if (!storedPassword.equals(password)) {
                System.out.println("Retrieve fallito: password errata");
                closeConnection();
                return null;
            }
            User.Level result = User.Level.valueOf(userDoc.getString("user_level"));
            System.out.println("Retrieve del livello fatto con successo !");
            closeConnection();
            return result;
        } catch (Exception e) {
            System.out.println("Errore generale durante il login");
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    public static boolean deleteUser(String nickname){
        openConnection("Users");
        try{
            collection.deleteOne(Filters.eq("nickname", nickname));
            System.out.println("Utente " + nickname + " eliminato correttamente !");
            closeConnection();
            return true;
        }catch(Exception e){
            System.out.println("Errore nell'eliminare l'utente");
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public static boolean updateUser(String nickname, String field, String value){
        openConnection("Users");

        if (field.equals("phone")) {
            long phoneValue = Long.parseLong(value);
            try {
                Bson update = Updates.set(field, phoneValue);
                collection.updateOne(eq("nickname", nickname), update);
                System.out.println("User aggiornato correttamente (phone)!");
                closeConnection();
                return true;
            } catch (Exception e) {
                System.out.println("Errore nell'aggiornamento dell'user (phone)");
                e.printStackTrace();
                closeConnection();
                return false;
            }
        }
        if (field.equals("user_level")) {
            User.Level levelValue = User.Level.valueOf(value.toUpperCase());
            try {
                Bson update = Updates.set(field, levelValue.toString());
                collection.updateOne(eq("nickname", nickname), update);
                System.out.println("User aggiornato correttamente (user_level)!");
                closeConnection();
                return true;
            } catch (Exception e) {
                System.out.println("Errore nell'aggiornamento dell'user (user_level)");
                e.printStackTrace();
                closeConnection();
                return false;
            }
        }
        try {
            Bson update = Updates.set(field, value);
            collection.updateOne(eq("nickname", nickname), update);
            System.out.println("User aggiornato correttamente (string)!");
            closeConnection();
            return true;
        } catch (Exception e) {
            System.out.println("Errore nell'aggiornamento dell'user (string)");
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public static PaymentInfo getPaymentFromUsername(String nickname){
        openConnection("Users");
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            Document doc = collection.find(eq("nickname", nickname)).first();
            if (doc == null){
                System.out.println("Nessun utente memorizzato che abbia quel"
                        + "nickname");
                closeConnection();
                return null;
            }
            System.out.println("Retrieve del pagamento per l'utente " + nickname + " effettuato con successo !");
            String json = doc.toJson();
            JsonNode rootNode = deserialize.readTree(json);
            JsonNode paymentNode = rootNode.get("payment");
            if(paymentNode == null){
                System.out.println("L'utente non ha un metodo di pagamento memorizzato");
                closeConnection();
                return null;
            }
            PaymentInfo paymentinfo = deserialize.treeToValue(paymentNode, PaymentInfo.class);
            closeConnection();
            return paymentinfo;
        }catch(Exception e){
            System.out.println("Errore nell'ottenere il metodo di pagamento");
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    public static boolean loginUser(String nickname, String password) {
        openConnection("Users");
        try {
            Document userDoc = collection.find(eq("nickname", nickname)).first();
            if (userDoc == null) {
                System.out.println("Login fallito: nickname non trovato");
                closeConnection();
                return false;
            }
            String storedPassword = userDoc.getString("password");
            if (!storedPassword.equals(password)) {
                System.out.println("Login fallito: password errata");
                closeConnection();
                return false;
            }
            System.out.println("Login avvenuto con successo per nickname: " + nickname);
            closeConnection();
            return true;
        } catch (Exception e) {
            System.out.println("Errore generale durante il login");
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    //+-------------------CRUD OPERATIONS FOR ORDER CLASS----------------------+
    public static boolean addWineOrder(String nickname, Order order) {
        openConnection("Users");
        try {
            Document userDoc = collection.find(eq("nickname", nickname)).first();
            if (userDoc == null) {
                System.out.println("Impossibile aggiungere l'ordine, utente non esistente");
                closeConnection();
                return false;
            }
            @SuppressWarnings("unchecked")
            List<Document> oldOrders = (List<Document>) userDoc.get("orders");

            Document orderDoc = new Document();
            orderDoc.append("id_order", order.getIdOrder());
            orderDoc.append("confirmation_date", order.getConfirmationDate());
            orderDoc.append("delivery_date", order.getDeliveryDate());
            orderDoc.append("order_total_cost", order.getOrderTotalCost());

            List<Document> orderListDocs = new ArrayList<>();
            for (OrderList item : order.getOrderElements()) {
                Document itemDoc = new Document();
                itemDoc.append("wine_id",     item.getWine_id());
                itemDoc.append("wine_name",   item.getWine_name());
                itemDoc.append("price",       item.getPrice());
                itemDoc.append("wine_number", item.getWine_number());
                orderListDocs.add(itemDoc);
            }
            orderDoc.append("order_list", orderListDocs);

            Bson updateCmd;
            if (oldOrders != null && !oldOrders.isEmpty()) {
                updateCmd = Updates.push("orders", orderDoc);
            } else {
                List<Document> newOrdersArray = new ArrayList<>();
                newOrdersArray.add(orderDoc);
                updateCmd = Updates.set("orders", newOrdersArray);
            }
            collection.updateOne(eq("nickname", nickname), updateCmd);
            closeConnection();
            System.out.println("Ordine aggiunto correttamente");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public static ArrayList<Order> retrieveUserOrders(String nickname){
        openConnection("Users");
        ArrayList<Order> orders = new ArrayList<>();
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            Document userDoc = collection.find(eq("nickname", nickname)).first();
            if(userDoc == null){
                System.out.println("Nessun utente trovato con quel soprannome");
                return null;
            }
            User user = deserialize.readValue(userDoc.toJson(), User.class);
            if(user.getOrders() != null && !user.getOrders().isEmpty()){
                orders.addAll(user.getOrders());
            }else{
                System.out.println("L'utente non ha nessun ordine");
                closeConnection();
                return null;
            }
        }catch(Exception e){
            System.out.println("Errore nel ritornare gli ordini");
            e.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return orders;
    }

    public static Order_Insert retrieveIdAndPrice(String wineName, String wineryName){
        openConnection("Wines");
        try{
            Bson filter = Filters.and(
                    Filters.eq("name", wineName),
                    Filters.eq("winery.name", wineryName)
            );
            Bson projection = Projections.fields(
                    Projections.include("_id", "price")
            );
            Document doc = collection.find(filter).projection(projection).first();
            if (doc == null){
                System.out.println("Nessun vino con quelle caratteristiche, "
                        + "inserimento ordine impossibile");
                return null;
            }
            String idString = doc.get("_id").toString();
            int price = doc.getInteger("price");
            closeConnection();
            return new Order_Insert(idString, price);
        }catch(Exception e){
            System.out.println("Errore generale nel retrieve dell'id e price");
            closeConnection();
            return null;
        }
    }

    public static boolean removeWineOrder(String nickname, String orderId) {
        openConnection("Users");
        try {
            Bson pullFilter = Filters.eq("id_order", orderId);
            Bson update = Updates.pull("orders", pullFilter);
            collection.updateOne(eq("nickname", nickname), update);
            closeConnection();
            System.out.println("Ordine con id_order=" + orderId + " rimosso per utente " + nickname);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    //+--------------------CRUD OPERATIONS FOR WINE CLASS ---------------------+
    public static boolean addWine(AbstractWine wine){
        openConnection("Wines");
        try{
            
            //Camp comuni alla abstract
            Document doc = new Document();
            doc.append("_id", wine.getId());
            doc.append("name", wine.getName());
            doc.append("price", wine.getPrice());
            doc.append("alcohol_percentage", wine.getAlcohol_percentage());
            doc.append("description", wine.getDescription());
            doc.append("country", wine.getCountry());
            doc.append("region", wine.getRegion());
            doc.append("provenance", wine.getProvenance());
            doc.append("variety", wine.getVariety());

            if(wine.getProvenance().equals("W")){
                
                Wine_WineMag winemag = (Wine_WineMag) wine;
                
                Document winery = new Document();
                
                winery.append("id", winemag.getWinery_id());
                winery.append("name", winemag.getWinery_name());
                doc.append("Winery", winery);
            }
            else if(wine.getProvenance().equals("V")){
                
                Wine_WineVivino vivino = (Wine_WineVivino) wine;
                Document docWinery = new Document();
                
                docWinery.append("id", vivino.getWinery_id());
                docWinery.append("name", vivino.getWinery_name());
                doc.append("winery", docWinery);

                Document tasteDoc = new Document();
                Document structureDoc = new Document();
                
                structureDoc.append("acidity",   vivino.getAcidity());
                structureDoc.append("fizziness", vivino.getFizziness());
                structureDoc.append("intensity", vivino.getIntensity());
                structureDoc.append("sweetness", vivino.getSweetness());
                structureDoc.append("tannin",    vivino.getTannin());
                tasteDoc.append("structure", structureDoc);

                List<Document> flavorArray = new ArrayList<>();
                
                if (vivino.getFlavorList() != null) {
                    for (Wine_WineVivino.Flavor fl : vivino.getFlavorList()) {
                        Document flDoc = new Document();
                        flDoc.append("group", fl.getGroup());
                        flDoc.append("mentions_count", fl.getMentions_count());
                        flavorArray.add(flDoc);
                    }
                }
                
                tasteDoc.append("flavor", flavorArray);
                doc.append("taste", tasteDoc);

                Document styleDoc = new Document();
                styleDoc.append("body", vivino.getBody());
                styleDoc.append("body_description", vivino.getBody_description());

                List<Document> foodArray = new ArrayList<>();
                
                if (vivino.getFoodList() != null) {
                    for (Wine_WineVivino.Food fd : vivino.getFoodList()) {
                        Document fdDoc = new Document();
                        fdDoc.append("name", fd.getName());
                        foodArray.add(fdDoc);
                    }
                }
                
                styleDoc.append("food", foodArray);
                doc.append("style", styleDoc);
            }

            collection.insertOne(doc);
            System.out.println("Vino inserito correttamente.");
            closeConnection();
            return true;
        }catch(Exception e){
            System.out.println("Errore generale nell'inserimento del vino");
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public static boolean deleteWine(String id){
        openConnection("Wines");
        try{
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("Vino eliminato correttamente !");
            closeConnection();
            return true;
        }catch(Exception e){
            System.out.println("Errore nel cancellare il vino");
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }

    public static AbstractWine getWineById(String id){
        openConnection("Wines");
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            Document doc = collection.find(eq("_id", id)).first();
            if(doc == null){
                System.out.println("Nessun vino presente con _id=" + id);
                return null;
            }
            String provenance = doc.getString("provenance");
            if(provenance == null){
                System.out.println("Documento privo di 'provenance'");
                closeConnection();
                return null;
            }
            AbstractWine result;
            if(provenance.equals("W")){
                result = deserialize.readValue(doc.toJson(), Wine_WineMag.class);
            } else {
                result = deserialize.readValue(doc.toJson(), Wine_WineVivino.class);
            }
            closeConnection();
            return result;
        }catch(Exception e){
            System.out.println("Errore nella lettura del vino dal database");
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    public static List<AbstractWine> getWinesByFilter(String field, String value, String provenance){
        openConnection("Wines");
        
        List<AbstractWine> resultList = new ArrayList<>();
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            Bson baseFilter = eq("provenance", provenance);
            Bson secondaryFilter = null;

            switch(field){
                case "price":
                    int priceVal = Integer.parseInt(value);
                    secondaryFilter = Filters.lte("price", priceVal);
                    break;
                    
                case "alcohol_percentage":
                    int alcVal = Integer.parseInt(value);
                    secondaryFilter = Filters.lte("alcohol_percentage", alcVal);
                    break;
                    
                case "country":
                    secondaryFilter = eq("country", value);
                    break;
                    
                default:
                    System.out.println("Campo non valido per la provenance " + provenance + ": " + field);
                    closeConnection();
                    return resultList;
            }

            Bson finalFilter = Filters.and(baseFilter, secondaryFilter);
            List<Document> docs = collection.find(finalFilter).into(new ArrayList<>());

            for(Document d : docs){
                
                String prov = d.getString("provenance");
                
                if(prov.equals("W")){
                    Wine_WineMag wineMag = deserialize.readValue(d.toJson(), Wine_WineMag.class);
                    resultList.add(wineMag);
                    
                } else {
                    Wine_WineVivino wineViv = deserialize.readValue(d.toJson(), Wine_WineVivino.class);
                    resultList.add(wineViv);
                }
            }
            closeConnection();
            return resultList;
        }catch(Exception e){
            e.printStackTrace();
            closeConnection();
            return null;
        }
    }

    public static ArrayList<Document> getWinesByWineryName(String wineryName, String provenance){
        openConnection("Wines");
        ArrayList<Document> results = new ArrayList<>();
        try {
            List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.and(eq("provenance", provenance), eq("winery.name", wineryName))),
                Aggregates.group("$winery.name",
                    Accumulators.push("wines", "$name")
                ),
                Aggregates.project(Projections.fields(
                    Projections.excludeId(),
                    Projections.computed("winery", "$_id"),
                    Projections.computed("wines", "$wines")
                ))
            );
            for (Document doc : collection.aggregate(pipeline)) {
                results.add(doc);
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'aggregazione per cantina " + wineryName + " e provenienza " + provenance);
            e.printStackTrace();
        }
        closeConnection();
        return results;
    }

    public static ArrayList<AbstractWine> getWineByName(String name){
        openConnection("Wines");
        
        try{
            ObjectMapper deserialize = new ObjectMapper();
            deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            MongoCursor <Document> cursor = collection.find(eq("name", name)).iterator();
            
            ArrayList <AbstractWine> resultsreturn = new ArrayList<>();
            String provenance;
            
            //Possibile avere più risultati se prendo solo in base al nome
            while(cursor.hasNext()){
                Document doc = cursor.next();
                provenance = doc.getString("provenance");
                
                if(provenance.equals("W")){
                    resultsreturn.add(deserialize.readValue(doc.toJson(), Wine_WineMag.class));
                }
                
                if(provenance.equals("V")){
                    resultsreturn.add(deserialize.readValue(doc.toJson(), Wine_WineVivino.class));
                }
            }
                            
            closeConnection();
            
            return resultsreturn;
        
        }catch(Exception e){
            System.out.println("Errore nel fare il retrieve del vino dal nome");
            return null;
        }
    }
    
    //+-----------------------------STATISTICHE ADMIN------------------------------+

    public static ArrayList<Document> getGenderDistribution() {
        openConnection("Users");
        ArrayList<Document> results = new ArrayList<>();
        Bson group = group("$gender", sum("total", 1));
        Bson projectFields = project(fields(
                excludeId(),
                computed("Gender", "$_id"),
                computed("Total", "$total")
        ));
        try {
            for (Document doc : collection.aggregate(Arrays.asList(group, projectFields))) {
                results.add(doc);
            }
        } catch (Exception e) {
            System.out.println("Errore nell'aggregazione sui generi");
        }
        closeConnection();
        return results;
    }

    public static ArrayList<Document> getRegionDistribution(){
        openConnection("Wines");
        ArrayList<Document> results = new ArrayList<>();
        Bson group = group("$region", sum("total", 1));
        Bson projectFields = project(fields(
                excludeId(),
                computed("Region", "$_id"),
                computed("Total", "$total")
        ));
        try{
            for(Document doc: collection.aggregate(Arrays.asList(group, projectFields))){
                results.add(doc);
            }
        }catch(Exception e){
            System.out.println("Aggregation su region fallita");
            e.printStackTrace();
        }
        return results;
    }

    public static long countUniqueWineNames() {
        openConnection("Wines");
        long count = 0;
        List<Bson> aggregationPipeline = Arrays.asList(
            group("$name"),
            count("uniqueNameCount")
        );
        try {
            AggregateIterable<Document> result = collection.aggregate(aggregationPipeline);
            if (result.first() != null) {
                count = result.first().getInteger("uniqueNameCount");
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'aggregazione per contare i nomi unici dei vini: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return count;
    }

    public static ArrayList<Document> getPriceBuckets() {
        openConnection("Wines");
        ArrayList<Document> results = new ArrayList<>();
        try {
            List<Bson> pipeline = Arrays.asList(
                bucket(
                    "$price",
                    Arrays.asList(0, 25, 50, 75, 100),
                    new BucketOptions()
                        .defaultBucket(">100")
                        .output(new BsonField("count", new Document("$sum", 1)))
                ),
                project(
                    fields(
                        excludeId(),
                        computed("Fascia", "$_id"),
                        computed("Numero Vini", "$count")
                    )
                )
            );
            for (Document doc : collection.aggregate(pipeline)) {
                results.add(doc);
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'esecuzione della pipeline");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return results;
    }

    public static double getAvgOrderCost() {
        openConnection("Users");
        double averageCost = 0.0;
        try {
            List<Bson> pipeline = Arrays.asList(
                unwind("$orders"),
                group(null, Accumulators.avg("avgCost", "$orders.order_total_cost")),
                project(
                    fields(
                        excludeId(),
                        computed("AverageOrderCost", "$avgCost")
                    )
                )
            );
            Document resultDoc = collection.aggregate(pipeline).first();
            if (resultDoc != null) {
                averageCost = resultDoc.getDouble("AverageOrderCost");
            } else {
                System.out.println("Nessun ordine presente o impossibile calcolare la media");
            }
        } catch (Exception e) {
            System.out.println("Errore nel calcolare la media degli ordini: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return averageCost;
    }
    
    public static List<AbstractWine> getBestSellingWineOfTheMonth() {
    openConnection("Users");
    try {
        LocalDate now = LocalDate.now();
        LocalDate low_interval = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate high_interval = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        List<Bson> pipeline = Arrays.asList(
                
            Aggregates.unwind("$orders"),
            
            Aggregates.match(
                Filters.and(
                    Filters.gte("orders.confirmation_date", low_interval.toString()),
                    Filters.lte("orders.confirmation_date", high_interval.toString())
                )
            ),
            
            Aggregates.unwind("$orders.order_list"),
            Aggregates.group(
                "$orders.order_list.wine_id",
                Accumulators.sum("vino_comprato", "$orders.order_list.wine_number")
            ),
            Aggregates.sort(Sorts.descending("vino_comprato")),
            Aggregates.project(
                fields(
                    computed("id_vino", "$_id"),
                    computed("Numero bottiglie", "$vino_comprato")
                )
            )
        );

        List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
        
        if (results.isEmpty()) {
            System.out.println("Nessun vino venduto nell'ultimo mese");
            closeConnection();
            return null;
        
        } else {
            
            int max = results.get(0).getInteger("Numero bottiglie");
            List<AbstractWine> bestSelledWines= new ArrayList<>();
            
            //Ci può essere un caso limite dove abbiamo dei risultati parimerito
            for (Document doc : results) {
                
                if (doc.getInteger("Numero bottiglie") == max) {
                    bestSelledWines.add(getWineById(doc.getString("id_vino")));
                
                } else {
                    break;
                }
            }
            closeConnection();
            return bestSelledWines;
        }
    } catch (Exception e) {
        System.out.println("Errore generale nell'eseguire l'operazione");
        e.printStackTrace();
        closeConnection();
        return null;
    }
}
}
