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
    private static String MONGO_DBNAME = "lsmdb";
    private static String MONGO_CONNECTION_STRING = "mongodb://10.1.1.19:27020,10.1.1.18:27020,10.1.1.17:27020/?retryWrites=true&w=majority&wtimeout=10000"; 
    //GETTER AND SETTER METHOD FOR THE CONFIG CLASS
    public static String getMongoConnectionString(){
        return MONGO_CONNECTION_STRING;
    }
    
    public static String getMongoDbName(){
        return MONGO_DBNAME;
    }

    public static void setMONGO_DBNAME(String MONGO_DBNAME) {
        Driver_Config.MONGO_DBNAME = MONGO_DBNAME;
    }

    public static void setMONGO_CONNECTION_STRING(String MONGO_CONNECTION_STRING) {
        Driver_Config.MONGO_CONNECTION_STRING = MONGO_CONNECTION_STRING;
    }
    
    
    
    
    
}
