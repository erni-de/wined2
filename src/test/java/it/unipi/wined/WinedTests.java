/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package it.unipi.wined;


import it.unipi.wined.bean.User;
import it.unipi.wined.bean.PaymentInfo;

import it.unipi.wined.driver.Mongo;
//import it.unipi.wined.json.objects.FakeUser;
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
    public void insertReview(){
        User user = new User("erni", "delia");
        user.setLevel(User.level.ADMIN);
        System.out.println(user.toString());

    }
   */
    @Test
    public void Insert_New_User_MongoDB(){
        
        PaymentInfo provapay = new PaymentInfo("8484884", 234, "12/2028");
        
        User prova = new User("Berti", "Nicola", "zio_perone@hotmail.it", 
                222333444, "08/06/1998", "Maschio", "Via No caramba 12", 
                "nick98", "1234", User.Level.REGULAR, provapay, null);
        
        Mongo.addUser(prova);
    }
    
    @Test
    public void Login_User_MongoDB(){
        String nickname = "Elody_Bashirian";
        String password = "Elody2024";
        
        Mongo.loginUser(nickname, password);
    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}
