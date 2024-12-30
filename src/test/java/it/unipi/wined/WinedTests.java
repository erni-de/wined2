/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import it.unipi.wined.json.objects.FakeUser;
import it.unipi.wined.neo4j.Neo4JUtils;
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
/*
    @Test
    public void testUsers(){
        Neo4JUtils w = new Neo4JUtils();
        List<FakeUser> fu = w.getUsers("/home/erni/data/users/female_users.json", "/home/erni/wined/wined/data/users/male_users.json");
        FakeUser.printUsers(fu);
    }
    
    
    @Test
    public void testNeo4J(){
        Neo4JUtils w = new Neo4JUtils();
        w.insertVivinoJson("/home/erni/data/final_dataset/vivino_data.json");
        w.insertWinemagJson("/home/erni/data/final_dataset/winemag_data.json");
        
    }
    
    
    @Test 
    public void insertReview(){
        Neo4JUtils w = new Neo4JUtils();
        List<FakeUser> fu = w.getUsers("/home/erni/data/users/female_users.json", "/home/erni/wined/wined/data/users/male_users.json");
        Review review = new Review("shit wine", (float) 1.0, new User(fu.get(1)));
        w.insertReview("Domaine Ehrhart 2013 Domaine Saint-Rémy Herrenweg Gewurztraminer (Alsace)", review);

    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
*/
}
