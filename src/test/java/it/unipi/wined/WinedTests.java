/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package it.unipi.wined;

import it.unipi.wined.bean.User;
import it.unipi.wined.bean.PaymentInfo;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;

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
    
    //OK FUNZIONANTE
    @Test
    public void Insert_New_User_MongoDB(){
        
        PaymentInfo provapay = new PaymentInfo("8484884", 234, "12/2028");
        
        User prova = new User("Berti", "Nicola", "zio_perone@hotmail.it", 
                222333444, "08/06/1998", "Maschio", "Via No caramba 12", 
                "nick98", "1234", User.Level.REGULAR, provapay, null);
        
        Mongo.addUser(prova);
    }
    
    //OK FUNZIONANTE
    @Test
    public void Login_User_MongoDB(){
        String nickname = "Elody_Bashirian";
        String password = "Elody2024";
        
        Mongo.loginUser(nickname, password);
    }
    
    //OK FUNZIONANTE
    @Test
    public void Retrieve_User_MongoDB(){
        User user = Mongo.RetrieveUser("Eryn_Bradtke");
        System.out.println(user);
    }
    
    //OK FUNZIONANTE
    @Test
    public void Delete_User_MongoDB(){
        boolean result = Mongo.deleteUser("Lulu_Medhurst");
        if(result){
            System.out.println("Cancellazione avvenuta con successo");
        }else{
            System.out.println("C'è stato un problema");
        }
    }
    
    //OK FUNZIONANTE
    @Test
    public void Update_User_MongoDB(){
        boolean result = Mongo.updateUser("Zena_Marvin", "gender", "Donald Trump");
        
        if(result){
            System.out.println("OK UPDATE");
        }else{
            System.out.println("NO UPDATE");

        }
        
        //CASI SPECIALI
        result = Mongo.updateUser("Laila_Beer", "phone", "333333333333");

           if(result){
            System.out.println("OK UPDATE");
        }else{
            System.out.println("NO UPDATE");

        }
           
        result = Mongo.updateUser("Laila_Beer", "user_level", "PREMIUM");

           if(result){
            System.out.println("OK UPDATE");
        }else{
            System.out.println("NO UPDATE");

        }
    }
    
    //OK FUNZIONANTE
    @Test
    public void get_Payment_Method(){
        PaymentInfo obj = Mongo.getPaymentFromUsername("Laila_Beer");
        System.out.println(obj);
    }
    
    //OK FUNZIONANTE
    @Test
    public void addWineMagWine(){
        Wine_WineMag wine = new Wine_WineMag("frfifeinfineri",
            "Tavernello",
            1,
            "Rosso",
            "Sconosciuta",
            "Un vino che non è vino",
            "Italia(Forse)",
            "8.5%",
            "W",
            "1",
            "MeStesso",
            "Toscana",
            "465656646456",
            "TavernelloCantine");
        
        boolean result = Mongo.addWineWineMag(wine);
        
        if(result){
            System.out.println("Tutto ok");
        }else{
            System.out.println("Failed Insert");
        }
    }
    
    //OK FUNZIONANTE
    @Test
    public void deleteWineMagWine(){
        String id = "57014aff-ec4b-417f-b9df-1b6a4ab2b6f0";
       
        boolean result = Mongo.deleteWineMagWine(id);
       
        if(result){
            System.out.println("Vino cancellato correttamente");
        }else{
            System.out.println("Errore nel cancellamento");
        }
    }
    
    //OK FUNZIONANTE
    @Test
    public void getWineMagWineById(){
        String id = "9791a6d2-ac81-45fc-874d-a73bdd52baab";
        
        Wine_WineMag result;
        result = Mongo.getWineMagWineById(id);
        
        System.out.println(result);
        
        //Da qui si estrapolano direttamente i dati della cantina
        System.out.println(result.getWinery_name() + " " + result.getWinery_id());
    }
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}
