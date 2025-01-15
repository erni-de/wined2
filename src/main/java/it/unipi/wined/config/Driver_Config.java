/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.config;

/**
 *
 * @author nicol
 */
public class Driver_Config {

    //LOCAL REPLICA CONFIGURATION
    private static final String MONGO_DBNAME = "WinedDB";
    private static final String MONGO_CONNECTION_STRING =         "mongodb://localhost:27018,localhost:27019,localhost:27020/?retryWrites=true&w=majority&wtimeout=10000";

    //GETTER AND SETTER METHOD FOR THE CONFIG CLASS
    public static String getMongoConnectionString(){
        return MONGO_CONNECTION_STRING;
    }
    
    public static String getMongoDbName(){
        return MONGO_DBNAME;
    }
    
    //POI QUA LA PARTE DI NEO4J, SE SERVE USERNAME E PASSWORD
    //SI METTONO QUA O ALTRA ROBA
}
