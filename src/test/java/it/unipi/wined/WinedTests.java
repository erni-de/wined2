/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.unipi.wined.bean.AbstractWine;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.bean.UserAggregationOrder;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.bean.Neo4jListWrapper;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class WinedTests {
/*
    @Test
    public void test() throws JsonProcessingException {
        ArrayList<Document> docs = Mongo.getGenderDistribution();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(docs.toString());
        for (Document doc : docs) {
            JsonNode jsonNode = mapper.readTree(doc.toJson());
            System.out.println(jsonNode.get("Gender").asText() + " " + jsonNode.get("Total").asText());
        }
    }

    @Test
    public void test3() throws JsonProcessingException {
        ArrayList<Document> docs = Mongo.getRegionDistribution();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(docs.toString());
        for (Document doc : docs) {
            JsonNode jsonNode = mapper.readTree(doc.toJson());
            System.out.println(jsonNode.get("Region").asText() + " " + jsonNode.get("Total").asText());
        }
    }

    @Test
    public void test2() throws JsonProcessingException {
        ArrayList<Document> docs = Mongo.getPriceBuckets();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(docs.toString());
        for (Document doc : docs) {
            JsonNode jsonNode = mapper.readTree(doc.toJson());
            System.out.println(jsonNode.get("Fascia").asText() + " " + jsonNode.get("Numero Vini").asText());
        }
    }

    @Test
    public void testd() {
        Gson gson = new Gson();
        ArrayList<Object> a = new ArrayList<>();
        a.add(new User("bill", "clinton", "billie", 3253, "fwew", "wfwef", "fwewe", "wegge", "wgr", User.Level.REGULAR));
        a.add(new Review("wine", "rating", "nice", "dick", "donnu"));
        String json = gson.toJson(a);

        Object[] o = gson.fromJson(json, Object[].class);
        User user = gson.fromJson(gson.toJson(o[0]), User.class);
        Review rev = gson.fromJson(gson.toJson(o[1]), Review.class);

        System.out.println(user.getNickname() + " " + rev.wine);

    }

 */
    @Test
    public void testGetUsersWithAtLeastNOrders() {
        System.out.println("Dioboia");

            System.out.println("Dioboia");

            
    int n = 1;
    
    ArrayList<UserAggregationOrder> results = Mongo.getUserWithAtLeastNOrders(n);
    
    System.out.println("Dioboia");
    if (results != null && !results.isEmpty()) {
        
        System.out.println("Utenti con almeno " + n + " ordini:");
        
        for (UserAggregationOrder res : results) {
            System.out.println(res);
        }
    } else {
        System.out.println("Nessun utente con almeno " + n + " ordini, oppure errore in aggregazione.");
    }
}

    @Test
    public void testG() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("Les Vignes d'Alexandre 2009 Red (Châteauneuf-du-Pape)");
        arr.add("Madrigal 2012 Estate Tempranillo (Calistoga)");
        for (org.neo4j.driver.Record r : Neo4jGraphInteractions.getSuggestedWinesByFilter("ernak", arr)) {
            System.out.println(r.get("w.name") + "");
        }
    }
    
}
