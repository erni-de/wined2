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
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;

//Import delle classi del nostro progetto
import it.unipi.wined.bean.Order;
import it.unipi.wined.config.Driver_Config;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.PaymentInfo;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.bean.OrderList;

//Import di Jackson
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

//Import di Java
import java.util.ArrayList;
import java.util.List;

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
        orderDoc.append("departure_date", order.getDepartureDate());
        orderDoc.append("delivery_date", order.getDeliveryDate());
        orderDoc.append("feedback", order.getFeedback());
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
                doc.append("points", wine.getPoints());
                doc.append("description", wine.getDescription());
                doc.append("tester_name", wine.getTaster_name());
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
        
}
