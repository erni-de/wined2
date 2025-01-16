/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j.interaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.json.objects.VivinoWine;
import it.unipi.wined.json.objects.VivinoWrapper;
import it.unipi.wined.json.objects.WinemagWine;
import static it.unipi.wined.neo4j.Neo4JUtils.connectionIp;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import static it.unipi.wined.spring.Access.currentUser;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Result;

/**
 *
 * @author erni
 */
public class Neo4jGraphInteractions {

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

    /**
    *Insert new winemag-json like wine node into neo4j graph istance
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

    public static void recomputeRating(String wine){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})-[:REVIEWED]->(r:review)
                               RETURN r.rating
                               """).
                withParameters(Map.of("wineName", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        String res = result.records().toString().replace("Record<", "").replace(">", "").replace("r.", "");
        Gson gson = new Gson();
        Review[] rev = gson.fromJson(res, Review[].class);
        int totrev = 0;
        float sumrev = 0;
        for(Review r : rev){
            sumrev = sumrev + Float.parseFloat(r.rating);
            totrev++;
        }
        float wineRating = sumrev/totrev;
        driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})
                               SET w.rating = $wineRating
                               """).
                withParameters(Map.of("wineName", wine, "wineRating", wineRating)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }
    
    /*
    Inserts review to a specific wine, from the current user
     */
    public static void insertReview(String wine, Review review, String username) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (a:wine {name: $wineName})
                               MATCH (c:user {username: $userName})
                               CREATE (b:review {title: $reviewTitle, text: $reviewCorpus, rating: $reviewRating})
                               CREATE (a)-[:REVIEWED]->(b)
                               CREATE (b)-[:WRITTEN_BY]->(c)
                               """).
                withParameters(Map.of("wineName", wine, "reviewTitle", review.title, "reviewCorpus", review.body, "reviewRating", review.rating, "userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        recomputeRating(wine); //to update the qine rating
    }
    
    public static void updateLikeCount(String wine){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (u:user)-[l:LIKES]->(w:wine {name: $wine})
                               WITH w, COUNT(l) as likes SET w.likes = likes
                               """).
                withParameters(Map.of("wine", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }
    
    public static void likeWine(String wine, String username) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})
                               MATCH (u:user {username: $userName})
                               CREATE (u)-[:LIKES]->(w)
                               """).
                withParameters(Map.of("wineName", wine,"userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        updateLikeCount(wine);
    }
    
    /*
    Inserts new user into graph (called when registering)
     */
    public static void addUserNode(User userToAdd) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               CREATE(u:user {id: $userId, username: $userName}) 
                               """).
                withParameters(Map.of("userId", userToAdd.id, "userName", userToAdd.getNickname())).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }
    
    public static void deleteUserNode(User userToDelete){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH(u:user {id: $userId, username: $userName})
                               DELETE u
                               """).
                withParameters(Map.of("userId", userToDelete.id, "userName", userToDelete.getNickname())).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }

    /*
    Create a follow relationship from the current user to a selected (target)
    user.
     */
    public static void followUser(String selfUsername, String targetUserName) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (a:user {username: $selfUser})
                               MATCH (b:user {username: $targetUser})
                               CREATE (a)-[:FOLLOWS]->(b)
                               """).
                withParameters(Map.of("selfUser", selfUsername, "targetUser", targetUserName)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }

    public static boolean checkIfUserExists(String username) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (n:user {username: $username})
                                RETURN CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END AS exists
                               """).
                withParameters(Map.of("username", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        return result.records().toString().equals("[Record<{exists: 1}>]");
    }

    public static boolean checkIfWineExists(String wine) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (n:wine {name: $winename})
                                RETURN CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END AS exists
                               """).
                withParameters(Map.of("winename", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        return !result.records().toString().equals("[]");
    }

    public static String usersReviewedWines(String user) {
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (w:wine)-[r:REVIEWED]->(b)-[d:WRITTEN_BY]->(u:user {username: $username})
                                RETURN w.name
                               """).
                withParameters(Map.of("username", user)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        return result.records().toString();
    }
    
    public static String usersReviews(String user){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (w:wine)-[r:REVIEWED]->(b)-[d:WRITTEN_BY]->(u:user {username: $username})
                                RETURN w.name, b.rating, b.text, b.title
                               """).
                withParameters(Map.of("username", user)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        return result.records().toString();
    }
    
    public static String wineReviews(String wine){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (w:wine {name : $wineName})-[r:REVIEWED]->(b)-[d:WRITTEN_BY]->(u:user)
                                RETURN b.rating, b.text, b.title, u.username
                               """).
                withParameters(Map.of("wineName", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        return result.records().toString();
    }
    
    public static void getSuggestedWines(String username){
        Driver driver = establishConnection("neo4j://" + connectionIp + ":7687", "neo4j", "cinematto123"); //use ifconfig to retrive private ip
        EagerResult result;
        result = driver.executableQuery("""
                                         MATCH (u:user {username : $userName})-[f:FOLLOWS]->(b:user)-[l:LIKES]->(w:wine)
                                         MATCH (v:wine)-[:REVIEWED]->(r:review)-[:WRITTEN_BY]->(b)
                                         RETURN b.rating, b.text, b.title, u.username
                                        """).
                withParameters(Map.of("userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }
    
}
