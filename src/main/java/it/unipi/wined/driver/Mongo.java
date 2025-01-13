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
import static com.mongodb.client.model.Filters.eq;

//Import delle classi del nostro progetto
import it.unipi.wined.bean.Order;
import it.unipi.wined.config.Driver_Config;
import it.unipi.wined.bean.User;
import org.bson.Document;

//Import di Jackson
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    
    
    //+--------------------OPERATIONS INTO THE USER COLLECTION---------------------+
    
    
    
    
    
    //+--------------------CRUD OPERATIONS INTO THE USER ---------------------+

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
}
