/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j.interaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.wined.Review;
import it.unipi.wined.User;
import it.unipi.wined.json.objects.VivinoWine;
import it.unipi.wined.json.objects.VivinoWrapper;
import it.unipi.wined.json.objects.WinemagWine;
import static it.unipi.wined.neo4j.Neo4JUtils.connectionIp;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import static it.unipi.wined.spring.Access.currentUser;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class GraphActions {
    
    /*
    Insert new vivino-json like wine node into neo4j graph istance
    */
    public static void insertVivinoJson(String filepath) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
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
    
    /*
    Insert new winemag-json like wine node into neo4j graph istance
    */
    public static void insertWinemagJson(String filepath) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
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
    
    /*
    Inserts review to a specific wine, from the current user
    */
    public static void insertReview(String wine ,Review review){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        User user = review.user; //this user should be the one making the review
        driver.executableQuery("""
                               MATCH (a:wine {name: $wineName})
                               CREATE (b:review {id: $reviewId, text: $reviewCorpus, rating: $reviewRating})
                               MATCH (c:user {id: $userId})
                               CREATE (a)-[:REVIEWED]->(b)
                               CREATE (b)-[:WRITTEN_BY]->(c)
                               """).
                        withParameters(Map.of("wineName", wine, "reviewId", review.id, "reviewCorpus", review.Text, "reviewRating", review.rating, "userId", currentUser.id)).
                        withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                        execute();
    }
    
    
    /*
    Inserts new user into graph (called when registering)
    */
    public static void addUserNode(){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               CREATE(a:user {id: $userId, firstname: $userFirstname, lastname: $userLastname}) 
                               """).
                            withParameters(Map.of("userId", currentUser.id, "userFirstname", currentUser.firstname, "userLastname", currentUser.lastname)).
                            withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                            execute();
    }
    
    /*
    Create a follow relationship from the current user to a selected (target)
    user.
    */
    public static void followUser(User target_user){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (a:user {id: $selfUser})
                               MATCH (b:user {id: $targetUser})
                               CREATE (a)-[:FOLLOWS]->(b)
                               """).
                            withParameters(Map.of("selfUser", currentUser.id, "targetUser", target_user.id)).
                            withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                            execute();
    }
}
