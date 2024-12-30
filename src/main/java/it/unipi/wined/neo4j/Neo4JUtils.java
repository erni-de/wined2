/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j;

import it.unipi.wined.json.objects.VivinoWine;
import it.unipi.wined.json.objects.VivinoWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.wined.Review;
import it.unipi.wined.User;
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
        
    public static Driver establishConnection(String uri, String user, String password) {
        return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public static void insertVivinoJson(String filepath) {
        Driver driver = establishConnection("neo4j://192.168.1.104:7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        Gson gson = new Gson();
        try {
            FileReader reader = new FileReader(filepath);
            Type userListType = new TypeToken<List<VivinoWrapper>>() {
            }.getType();
            List<VivinoWrapper> overall = gson.fromJson(reader, userListType);
            VivinoWrapper wines = overall.get(0);
            List<VivinoWine> wine = wines.getWines();
            for (VivinoWine v : wine) {
                v.id = WinemagWine.id_count;
                WinemagWine.id_count++;
                driver.executableQuery("CREATE (:wine {id: $id, name: $name})").
                        withParameters(Map.of("id", v.id, "name", v.name)).
                        withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                        execute();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static void insertWinemagJson(String filepath) {
        Driver driver = establishConnection("neo4j://192.168.1.104:7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        Gson gson = new Gson();
        try {
            FileReader reader = new FileReader(filepath);
            Type userListType = new TypeToken<List<WinemagWine>>() {
            }.getType();
            List<WinemagWine> wines = gson.fromJson(reader, userListType);
            for (WinemagWine v : wines) {
                v.id = WinemagWine.id_count;
                WinemagWine.id_count++;
                driver.executableQuery("CREATE (:wine {id: $id, name: $name})").
                        withParameters(Map.of("id", v.id, "name", v.title)).
                        withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                        execute();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    
    public static List<FakeUser> getUsers(String filepath){
        Gson gson = new Gson();
        List<FakeUser> fu = new Stack<>();
        try {
            FileReader reader = new FileReader(filepath);
            FakeUserWrapper overall = gson.fromJson(reader, FakeUserWrapper.class);
            fu = overall.getUsers();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fu;
    }
    
    public static List<FakeUser> getUsers(String filepath1, String filepath2){
        return Stream.concat(getUsers(filepath1).stream(), getUsers(filepath2).stream()).toList();
    }
    
    public static void insertReview(String wine ,Review review){
        Driver driver = establishConnection("neo4j://192.168.1.104:7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        User user = review.user;
        driver.executableQuery("""
                               MATCH (a:wine {name: $wineName})
                               CREATE (b:review {id: $reviewId, text: $reviewCorpus, rating: $reviewRating})
                               CREATE (c:user {id: $userId, firstname: $userFirstname, lastname: $userLastname})
                               CREATE (a)-[:REVIEWED]->(b)
                               CREATE (b)-[:WRITTEN_BY]->(c)
                               """).
                        withParameters(Map.of("wineName", wine, "reviewId", review.id, "reviewCorpus", review.Text, "reviewRating", review.rating, "userId", user.id, "userFirstname", user.firstname, "userLastname", user.lastname)).
                        withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                        execute();
    }
    
    
    
    

}
