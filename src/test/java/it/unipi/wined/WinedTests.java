/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.json.objects.FakeUser;
import it.unipi.wined.neo4j.Neo4JUtils;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import it.unipi.wined.spring.Actions;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author erni
 */
public class WinedTests {
    
    public WinedTests() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }


    @Test
    public void testes(){
        Gson gson = new Gson();
        String username = "The Williamsburg Winery 2014 Four Barrel Cuvée Red (Virginia)";
        System.out.println(username);
        Neo4jGraphInteractions.recomputeRating(username);    
    }
    
    @Test 
    public void insertReview(){

    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}
