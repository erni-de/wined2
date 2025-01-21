/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.io.FileReader;
import java.util.ArrayList;
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

    @Test
    public void test() throws JsonProcessingException {
        ArrayList<Document> docs = Mongo.getRegionDistribution();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(docs.toString());
        for (Document doc : docs) {
            JsonNode jsonNode = mapper.readTree(doc.toJson());
            System.out.println(jsonNode.get("Region").asText() + " " + jsonNode.get("Total").asText() );
        }
    }
    
    @Test
    public void test2() throws JsonProcessingException {
        ArrayList<Document> docs = Mongo.getPriceBuckets();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(docs.toString());
        for (Document doc : docs) {
            JsonNode jsonNode = mapper.readTree(doc.toJson());
            System.out.println(jsonNode.get("Fascia").asText() + " " + jsonNode.get("Numero Vini").asText() );
        }
    }
    
     @Test
    public void testd() {
        System.out.println(Neo4jGraphInteractions.usersReviews("Susie_Sporer"));
    }
    
}
