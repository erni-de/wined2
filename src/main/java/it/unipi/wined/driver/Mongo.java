/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.wined.driver;

//Import relative per usare MongoDB
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import org.bson.conversions.Bson;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.computed;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.count;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.eq;

//Import delle classi del nostro progetto
import it.unipi.wined.bean.Order;
import it.unipi.wined.config.Driver_Config;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.PaymentInfo;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.bean.OrderList;
import it.unipi.wined.bean.Order_Insert;

//Import di Jackson
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

//Import di Java
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author nicol
 */

public class Mongo {
    
    //Statics variables for definying the connection to the DBMS
    //Variable must be set with the parameters for local cluster
    //Or virtual cluster deployment 
    private static MongoClient myClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> collection;
   
    //+--------------------CONNECTION MANAGEMENT---------------------+
    
    private static final ConnectionString uri = new ConnectionString(
            Driver_Config.getMongoConnectionString()); 
    
    //Open a connection with a collection (in our case 2 collection in total)
    private static void openConnection(String collectionName){
        
        //A try catch block for managing errors and exception
        try{
            myClient = MongoClients.create(uri);
            db = myClient.getDatabase(Driver_Config.getMongoDbName());
            collection = db.getCollection(collectionName);
            System.out.println("Connection aperta correttamente a " + collectionName);
        
        }catch(Exception e){
            System.out.println("Errore nell'aprire la collection " + collection);
            //Stampo l'errore per vedere un attimo
            e.printStackTrace();
        }
    }
    
    //Close a connection with a collection
    private static void closeConnection(){
        
        //E' quindi connesso
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
          
            //Verifico che non esista un utente con quel nickname già inserito
            Document ext_User = collection.find(eq("nickname", user.getNickname())).first();
            
            //Utente già presente fallisco nel login
            if (ext_User != null){
                System.out.println("Un utente è già registrato con questo nickname,"
                        + "scegline un'altro");
                closeConnection();
                return false;
            }
            
            //Defining BSON new document to populate
            Document doc = new Document();
            
            //Inserting the field for the user
            doc.append("firstname", user.getFirstName());
            doc.append("lastname", user.getLastName());
            doc.append("email", user.getEmail());
            doc.append("phone", user.getPhone());
            doc.append("birthday", user.getBirthday());
            doc.append("gender", user.getGender());
            doc.append("address", user.getAddress());
            doc.append("nickname", user.getNickname());
            doc.append("password", user.getPassword());
            
            //Inserting the privilege level of the user
            //Faccio il controllo su NULL che si fa anche nella classe
            if(user.getUser_level() != null){
                doc.append("user_level", user.getUser_level().toString());
            }
            
            //Inserisco il payment che è un document annidato
            if(user.getPayment() != null){
                Document paymentDoc = new Document();
                paymentDoc.append("card_number", user.getPayment().getCardNumber());
                paymentDoc.append("CVV", user.getPayment().getCVV());
                paymentDoc.append("expire_date", user.getPayment().getExpirationDate());
                doc.append("payment", paymentDoc);
            }
            
            //Inserisco l'array vuoto degli ordini (all'inizio l'utente non ne ha)
            doc.append("orders", new ArrayList<Document>());
            
            //Inserisco nel DB
            collection.insertOne(doc);
            
            System.out.println("L'utente è stato aggiunto correttamente !");
        
        }catch(Exception e){
            
            System.out.println("Errore nel creare l'utente");
            closeConnection();
            return false;
        }
        
        //Ok new user added
        closeConnection();
        return true;
    }
   
    public static User RetrieveUser(String nickname){
        
        openConnection("Users");
        
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
                
        try{
            //Ho un solo risultato, mantengo unicità con il nickname 
            //assicurandonemene solo uno
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
            
            //Casto da stringa a ENUM
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

    //Caso phone casto a long
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

    //Caso user_level casto l'enum
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

    // Caso default (string)
    // (oppure se in futuro avrai date, boolean, ecc. gestisci di conseguenza)
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

    //Login User in MongoDB
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
            // push
            updateCmd = Updates.push("orders", orderDoc);
        } else {
            // set
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
    
    //PRIVILEGED
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
    public static boolean addWineWineMag(Wine_WineMag wine){
        
        //Qui avremo due tipi di inserimenti a seconda del vino che inseriamo
        Document doc = new Document();
    
            try{
                openConnection("Wines");
                //Dati vino winemag generali
                doc.append("_id", wine.getId());
                doc.append("description", wine.getDescription());
                doc.append("price", wine.getPrice());
                doc.append("variety", wine.getVariety());
                doc.append("province", wine.getProvince());
                doc.append("country", wine.getCountry());
                doc.append("alchol_percentage", wine.getAlcohol_percentage());
                doc.append("name", wine.getName());
                doc.append("region", wine.getRegion());
                doc.append("provenance", wine.getProvenance());
                
                //Dati cantina
                Document doc_winery = new Document();
                doc_winery.append("id", wine.getWinery_id());
                doc_winery.append("name", wine.getWinery_name());
            
                doc.append("Winery", doc_winery);
                
                collection.insertOne(doc);
                
                System.out.println("Vino di tipo WineMag inserito correttamente");
                closeConnection();
                return true;
                
            }catch(Exception e){
                System.out.println("Errore generale nell'inserimento del vino");
                e.printStackTrace();
                closeConnection();
                return false;
            }
    }
    
   public static boolean addWineWineVivino(Wine_WineVivino wine) {
    openConnection("Wines");
    
    try {
        
        Document doc = new Document();
        
        doc.append("_id", wine.get_id());         
        doc.append("name", wine.getName());
        doc.append("price", wine.getPrice());
        doc.append("alcohol_percentage", wine.getAlcohol_percentage());
        doc.append("description", wine.getDescription());
        doc.append("country", wine.getCountry());
        doc.append("region", wine.getRegion());
        doc.append("provenance", wine.getProvenance());  
        doc.append("variety", wine.getVariety());

        //winery
        Document wineryDoc = new Document();
        
        wineryDoc.append("id",   wine.getWinery_id());
        wineryDoc.append("name", wine.getWinery_name());
        doc.append("winery", wineryDoc);

        //taste
        Document tasteDoc = new Document();
        Document structureDoc = new Document();
        
        structureDoc.append("acidity",   wine.getAcidity());
        structureDoc.append("fizziness", wine.getFizziness());
        structureDoc.append("intensity", wine.getIntensity());
        structureDoc.append("sweetness", wine.getSweetness());
        structureDoc.append("tannin",    wine.getTannin());

        tasteDoc.append("structure", structureDoc);

        //flavor
        List<Document> flavorArray = new ArrayList<>();
        if (wine.getFlavorList() != null) {
            for (Wine_WineVivino.Flavor fl : wine.getFlavorList()) {
                Document flDoc = new Document();
                flDoc.append("group", fl.getGroup());
                flDoc.append("mentions_count", fl.getMentions_count());
                flavorArray.add(flDoc);
            }
        }
        tasteDoc.append("flavor", flavorArray);

        doc.append("taste", tasteDoc);

        //style
        Document styleDoc = new Document();
        styleDoc.append("body", wine.getBody());
        styleDoc.append("body_description", wine.getBody_description());

        //food
        List<Document> foodArray = new ArrayList<>();
        if (wine.getFoodList() != null) {
            for (Wine_WineVivino.Food fd : wine.getFoodList()) {
                Document fdDoc = new Document();
                fdDoc.append("name", fd.getName());
                foodArray.add(fdDoc);
            }
        }
        styleDoc.append("food", foodArray);

        doc.append("style", styleDoc);

        //Insert 
        collection.insertOne(doc);

        System.out.println("Vino di tipo Vivino inserito correttamente.");
        closeConnection();
        return true;

    } catch (Exception e) {
        System.out.println("Errore generale nell'inserimento del vino Vivino");
        e.printStackTrace();
        closeConnection();
        return false;
    }
}
    
    public static boolean deleteWineMagWine(String id){
        openConnection("Wines");
        
        try{
            collection.deleteOne(Filters.eq("_id", id));
            closeConnection();
            return true;
        }catch(Exception e){
            
            System.out.println("Errore nel cancellare il vino");
            e.printStackTrace();
            closeConnection();
            return false;
        }
    }
    
    public static boolean deleteWineVivinoWine(String id){
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
    
    public static Wine_WineVivino getWineVivinoWineById(String id) {
    openConnection("Wines");
    Wine_WineVivino result = null;

    ObjectMapper deserialize = new ObjectMapper();
    deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
        Document db_result = collection.find(eq("_id", id)).first();
        
        if (db_result != null) {
            result = deserialize.readValue(db_result.toJson(), Wine_WineVivino.class);
        } else {
            System.out.println("Nessun vino Vivino presente con _id=" + id);
        }

        closeConnection();
        return result;

    } catch (Exception e) {
        System.out.println("Errore nella lettura del vino Vivino dal database");
        e.printStackTrace();
        closeConnection();
        return null;
    }
}

    public static Wine_WineMag getWineMagWineById(String id){
        openConnection("Wines");
        Wine_WineMag result;
    
        ObjectMapper deserialize = new ObjectMapper();
        deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        try{
            //Ho un solo risultato, mantengo unicità con l'id assicurandonemene solo uno
            Document db_result = collection.find(eq("_id",id)).first();
            
            if(db_result != null){
                result = deserialize.readValue(db_result.toJson(), Wine_WineMag.class);
                closeConnection();
                return result;
                
            }else{
                System.out.println("Nessun vino è presente con quell'id");
                return null;
            }
            
        }catch(Exception e){
            System.out.println("Errore improvviso nella lettura dal database");
            e.printStackTrace();
            closeConnection();
            return null;
            
        }
    }
    
//+------------------------------QUERY PROGETTO--------------------------------+
    public static ArrayList<Wine_WineMag> getWineMagByFilter(String field, String value) {
    openConnection("Wines");

    ArrayList<Wine_WineMag> resultList = new ArrayList<>();
    
    ObjectMapper deserialize = new ObjectMapper();
    deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
        //Filtra i documenti che hanno "provenance" = "W"
        Bson baseFilter = eq("provenance", "W");

        Bson secondaryFilter = null;

        switch (field) {
            case "points":
                //points >= value
                int pointsVal = Integer.parseInt(value);
                secondaryFilter = Filters.gte("points", pointsVal);
                break;

            case "taster_name":
                //taster_name == value
                secondaryFilter = eq("taster_name", value);
                break;

            case "price":
                //price <= value 
                int priceVal = Integer.parseInt(value);
                secondaryFilter = Filters.lte("price", priceVal);
                break;

            case "country":
                //country == value
                secondaryFilter = eq("country", value);
                break;

            case "alcohol_percentage":
                //alcohol_percentage <= value
                int alcVal = Integer.parseInt(value);
                secondaryFilter = Filters.lte("alcohol_percentage", alcVal);
                break;

            default:
                System.out.println("Campo non valido per WineMag: " + field);
                closeConnection();
                return resultList;
        }

        //Combino il filtro iniziale (provenance == "W") con quello passato
        Bson finalFilter = Filters.and(baseFilter, secondaryFilter);

        //Query
        List<Document> docs = collection.find(finalFilter).into(new ArrayList<>());

        //Deserializzo in WineMag
        for (Document d : docs) {
            Wine_WineMag wine = deserialize.readValue(d.toJson(), Wine_WineMag.class);
            resultList.add(wine);
        }

        closeConnection();
        return resultList;

    } catch (Exception e) {
        e.printStackTrace();
        closeConnection();
        return null;
    }
}
   public static ArrayList<Wine_WineVivino> getWineVivinoByFilter(String field, String value) {
    openConnection("Wines");

    ArrayList<Wine_WineVivino> resultList = new ArrayList<>();
    ObjectMapper deserialize = new ObjectMapper();
    deserialize.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
        //provenance = "V"
        Bson baseFilter = eq("provenance", "V");

        Bson secondaryFilter = null;

        switch (field) {
            case "price":
                //price <= value
                int priceVal = Integer.parseInt(value);
                secondaryFilter = Filters.lte("price", priceVal);
                break;

            case "alcohol_percentage":
                //alcohol_percentage <= value
                int alcVal = Integer.parseInt(value);
                secondaryFilter = Filters.lte("alcohol_percentage", alcVal);
                break;

            case "country":
                //country == value
                secondaryFilter = eq("country", value);
                break;

            default:
                System.out.println("Campo non valido per Vivino: " + field);
                closeConnection();
                return resultList;
        }

        //Creo il filtro
        Bson finalFilter = baseFilter;
     
        finalFilter = Filters.and(baseFilter, secondaryFilter);
        
        //Query
        List<Document> docs = collection.find(finalFilter).into(new ArrayList<>());

        //Deserializzo in WineVivino
        for (Document d : docs) {
            Wine_WineVivino wine = deserialize.readValue(d.toJson(), Wine_WineVivino.class);
            resultList.add(wine);
        }

        closeConnection();
        return resultList;
        
    } catch (Exception e) {
        e.printStackTrace();
        closeConnection();
        return null;
    }
}
  
  //PIPELINE
  public static ArrayList<Document> getWinesByWineryName(String wineryName, String provenance){
    
    openConnection("Wines");

    ArrayList<Document> results = new ArrayList<>();

    try {
        List<Bson> pipeline = Arrays.asList(
            
            Aggregates.match(Filters.and(eq("provenance", provenance), eq("winery.name", wineryName))),
            
            Aggregates.group("$winery.name", 
            Accumulators.push("wines", "$name") //Accumula i nomi dei vini sotto il nome della cantina
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

//+-----------------------------STATISTICHE ADMIN------------------------------+

//AGGREGATION
// Aggregazione per ottenere la distribuzione dei generi
public static ArrayList<Document> getGenderDistribution() {
    openConnection("Users");

    ArrayList<Document> results = new ArrayList<>();

    //Raggruppo per il campo "gender"
    Bson group = group("$gender", sum("total", 1));

    //Escludo l'ID e rinomino i campi per l'output
    Bson projectFields = project(fields(
        excludeId(),
        computed("Gender", "$_id"),
        computed("Total", "$total")
    ));

    try {
        //Eseguo l'aggregazione e aggiungo i risultati alla lista
        for (Document doc : collection.aggregate(Arrays.asList(group, projectFields))) {
            results.add(doc);
        }
    } catch (Exception e) {
        System.out.println("Errore nell'aggregazione sui generi");
    }

    closeConnection();
    return results;
}

//AGGREGATION
public static ArrayList<Document> getRegionDistribution(){
    openConnection("Wines");
    
    ArrayList<Document> results = new ArrayList<>();
    
    //Raggruppo per la regione del vino
    Bson group = group("$region", sum("total", 1));
    
    //Escludo e rinomino come prima
    Bson projectFields = project(fields(
            excludeId(),    
            computed("Region", "$_id"),
            computed("Total", "$total"))
    );
    
    try{
        for (Document doc: collection.aggregate(Arrays.asList(group,projectFields))){
             results.add(doc);
        }
    }catch(Exception e){
        System.out.println("Aggregation su region fallita");
        e.printStackTrace();
    }
    
    return results;
}

//AGGREGATION
public static long countUniqueWineNames() {
    openConnection("Wines");

    long count = 0;

    //Uso l'aggregation perché può succedere che un vino compaia sia nei vini
    //Di tipo Vivino sia in quelli di tipo WineMag
    //Qua c'interessa il numero di vini UNICI 
    List<Bson> aggregationPipeline = Arrays.asList(
        group("$name"),  //Raggruppa per nome creando un gruppo per ciascun nome unico
        count("uniqueNameCount")  //Conta i gruppi unici formati
    );

    try {
        //E' il tipo che restituisce l'aggregate, prima scorrevo col for
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
           
        //Fase di bucket
        Aggregates.bucket(
            "$price", 
                Arrays.asList(0, 25, 50, 75, 100), 
                    new BucketOptions()
                        .defaultBucket(">100") 
                        .output(new BsonField("count", new Document("$sum", 1))) 
        ),
         
         //Fase di project
         Aggregates.project(
                Projections.fields(
                    excludeId(),
                    Projections.computed("Fascia", "$_id"),
                    Projections.computed("Numero Vini", "$count")     
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
            
                //Scompatta l'array in più document 
                //Se ho 3 ordini darà 3 document
                Aggregates.unwind("$orders"),
                
                Aggregates.group(null, Accumulators.avg("avgCost", "$orders.order_total_cost")),
                
                Aggregates.project(Projections.fields(
                    Projections.excludeId(),
                    Projections.computed("AverageOrderCost", "$avgCost")  
            ))
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


}
