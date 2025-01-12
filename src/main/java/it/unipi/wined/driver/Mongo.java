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
import it.unipi.wined.bean.User;
import org.bson.Document;

//Import degli altri metodi usati nel progetto
import it.unipi.wined.config.*;

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

    /**
     *
     * @param user
     * @return
     */

    public static boolean addUser(User user){
        
        openConnection("Users");
        
        try{
          
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
        
        }catch(Exception e){
            
            System.out.println("Errore nel creare l'utente");
            closeConnection();
            return false;
        }
        
        //Ok new user added
        closeConnection();
        return true;
    }
}
