/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j;

import it.unipi.wined.json.objects.VivinoWine;
import it.unipi.wined.json.objects.VivinoWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.json.objects.FakeUser;
import it.unipi.wined.json.objects.FakeUserWrapper;
import it.unipi.wined.json.objects.WinemagWine;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Stream;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class Neo4JUtils {
//192.168.1.12
    public static String connectionString = "neo4j://192.168.1.10:7687";
    public static String neo4j_user = "neo4j";
    public static String neo4j_password = "cinematto123";

    public static Driver establishConnection() {
        return GraphDatabase.driver(connectionString, AuthTokens.basic(neo4j_user, neo4j_password));
    }

    /**
     * Specify path of users documents
     *
     * @param path
     */
    public static void loadUsers(String path) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(path)) {
            // Map JSON to a Java object
            User[] users = gson.fromJson(reader, User[].class);
            for (User u : users) {
                driver.executableQuery("""
                               MERGE (u:user { username: $userName}) 
                               """).
                        withParameters(Map.of("userName", u.getNickname())).
                        withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                        execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Specify path of wines documents
     * @param path 
     */
    public static void loadWine(String path) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(path)) {
            // Map JSON to a Java object
            Wine_WineMag[] wines = gson.fromJson(reader, Wine_WineMag[].class);
            for (Wine_WineMag w : wines) {
                driver.executableQuery("""
                               MERGE (w:wine {name: $wineName}) 
                               """).
                        withParameters(Map.of("wineName", w.getName())).
                        withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                        execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
