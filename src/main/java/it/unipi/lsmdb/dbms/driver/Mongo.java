/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.lsmdb.dbms.driver;

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
    private static MongoCollection<org.bson.Document> collection;
   
    //+--------------------CONNECTION MANAGEMENT---------------------+
    
    /*
    Here we will have all the parameters for make the connection (i have to
    set correctly mongodb on local cluster for working correctly)
    Probabily i will use an external file for store all the information regarding
    the connection
    private static final String connectionString = "mongodb://" + ...
    ...                      ...                            ...
    
    FOR NEO4J AND FOR MONGODB ALSO we will use a file named like InfoConfig.java
    
    ... 
    
    ...
    
    ...
    */
   
    //For remember final is like const in C / C++ 
    private static final ConnectionString uri = new ConnectionString(connectionString); 
    
    //Open a connection with a collection (in our case 2 collection in total)
    private static void openConnection(String Collection){
        
        //A try catch block for managing errors and exception
        try{
            myClient = MongoClients.create(uri);
            db = myClient.getDatabase(InfoConfig.getMongoDbName());
            collection = db.getCollection(collection);
        }catch(Exception e){
            System.out.println("Errore nell'aprire la collection " + collection);
            System.out.println("Il tipo dell'errore e' " + e);
        }
    }
    
    //Close a connection with a collection
    private static void closeConnection(){
        try{
            
        }catch(exception e){
            System.out.println("Errore nel chiudere la connessione con la collection");
            System.out.println("Il tipo dell'errore è " + e);
        }
    }
    
    //+--------------------OPERATIONS INTO THE COLLECTION---------------------+
    
    
    
    
    
    //+--------------------CRUD OPERATIONS INTO THE USER ---------------------+

    public static boolean addUser(User user){
        
        //Establishing a connection with the collection
        OpenConnection("Users");
        
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
            
            
            
            
            
            
        }catch(Exception e){
            System.out.println("Errore nel creare l'utente");
            closeConnection();
            return False;
        }
        
        //Ok new user added
        closeConnection();
        return true;
    }
}
