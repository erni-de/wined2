/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package it.unipi.wined;

import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.PaymentInfo;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.bean.OrderList;
import it.unipi.wined.bean.Order;

import it.unipi.wined.driver.Mongo;
//import it.unipi.wined.json.objects.FakeUser;
import it.unipi.wined.neo4j.Neo4JUtils;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import it.unipi.wined.spring.Actions;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.UUID;
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
        User user = Mongo.RetrieveUser("Delphia_Rogahn");
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
            8,
            "W",
            1,
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
    
    //OK FUNZIONANTE
    @Test
    public void retrieveUserLevel(){
        User.Level result = Mongo.RetrieveUserLevel("Mozelle_Erdman", "Mozelle1970");
        System.out.println(result);
    }
    
    //OK FUNZIONANTE
    @Test
    public void addVivino(){
        
        String wineId = UUID.randomUUID().toString();
        String idWinery = UUID.randomUUID().toString();
        
        //Dati del vino
        String name = "Grand Estates Chardonnay";
        int price = 75;
        int alcoholPercentage = 18;
        String description = "Il vino bono per il rione darsena";
        String country = "IT";
        String region = "Viareggio";
        String provenance = "V";
        String variety = "White";

        //Dati della cantina
        String wineryId = idWinery;
        String wineryName = "Viareggio 12";

        //Dati del gusto
        Double acidity = 3.4851587;
        Double fizziness = null; 
        Double intensity = 3.986575;
        Double sweetness = 2.0111642;
        Double tannin = null; 

        //Flavors
        List<Wine_WineVivino.Flavor> flavors = Arrays.asList(
            new Wine_WineVivino.Flavor("oak", 1305),
            new Wine_WineVivino.Flavor("tree_fruit", 310),
            new Wine_WineVivino.Flavor("tropical_fruit", 226)
        );

        //Stile
        Integer body = 4;
        String bodyDescription = "Full-bodied";

        //Food pairings
        List<Wine_WineVivino.Food> foods = Arrays.asList(
            new Wine_WineVivino.Food("Pork"),
            new Wine_WineVivino.Food("Rich fish (salmon, tuna etc)"),
            new Wine_WineVivino.Food("Vegetarian"),
            new Wine_WineVivino.Food("Poultry")
        );

        //Creazione dell'oggetto Wine_WineVivino
        Wine_WineVivino wine = new Wine_WineVivino(
            wineId, name, price, alcoholPercentage, description, country, region, provenance, variety,
            wineryId, wineryName, acidity, fizziness, intensity, sweetness, tannin, flavors, body, bodyDescription, foods
        );
        
        boolean result = Mongo.addWineWineVivino(wine);
        
        if(result){
            System.out.println("Vino di tipo Vivino inserito corretamente");
        }else{
            System.out.println("Problemi nell'inserimento");
        }
    }
    
    //OK FUNZIONANTE
    @Test
    public void deleteVivinoWine(){
        String id = "93ee8c57-5737-4cae-83b6-acc8fada7d36";
        Mongo.deleteWineVivinoWine(id);
    }
    
    //OK FUNZIONANTE (C'HO MOCCOLATO L'ANIMA CON IL JACKSON A DESERIALIZZARE)
    @Test
    public void getVivinoById(){
        String id = "884d7d37-694a-4660-8c9a-6c2b99cee346";
        
        Wine_WineVivino wine = Mongo.getWineVivinoWineById(id);
        System.out.println(wine);
    }   
    
    //OK FUNZIONANTE
    @Test
    public void testAddWineOrder() {
    String nickname = "Delphia_Rogahn";

    Order order = new Order();
    //Anche qui ovviamente si usa lo UUID
    order.setIdOrder("ringori");
    order.setConfirmationDate("2024-01-31");
    order.setDepartureDate("2024-02-02");
    order.setDeliveryDate("2024-02-03");
    order.setFeedback(4.5);
    order.setOrderTotalCost(99.9);

    OrderList item1 = new OrderList();
    item1.setWine_id("2a78a67d-8b50-4184-aee3-1634c19022ed");
    item1.setWine_name("Willamette Valley Vineyards 2009 Estate Pinot Noir");
    item1.setPrice(15.5);
    item1.setWine_number(2);

    OrderList item2 = new OrderList();
    item2.setWine_id("1d0024c3-7b5d-490f-9b67-fe78248354a6");
    item2.setWine_name("Goats do Roam 2008 Goat-Roti Syrah-Viognier");
    item2.setPrice(45.0);
    item2.setWine_number(1);

    order.setOrderElements(item1);
    order.setOrderElements(item2);

    boolean result = Mongo.addWineOrder(nickname, order);
    if (result) {
        System.out.println("Ordine aggiunto correttamente!");
    } else {
        System.out.println("Problemi nell'aggiunta dell'ordine");
    }
}
    //OK FUNZIONANTE
    @Test
    public void removeOrderUser(){
        
        String nickname = "Delphia_Rogahn";
        String id_order = "ringori";
        
        Mongo.removeWineOrder(nickname, id_order);
    }
    
    //OK FUNZIONANTE
    @Test
    public void testGetWineMagByFilterPoints() {
    String field = "points"; 
    String value = "100";

    ArrayList <Wine_WineMag> wines = Mongo.getWineMagByFilter(field, value);
    
    if (wines == null || wines.isEmpty()) {
        System.out.println("Nessun WineMag trovato con points >= " + value);
    } else {
        System.out.println("Wines WineMag con points >= " + value);
        for (Wine_WineMag w : wines) {
            System.out.println(w);
        }
    }
    }

    //OK FUNZIONANTE
    @Test
    public void testGetWineVivinoByPrice() {
    String field = "price"; 
    String value = "10"; 

    ArrayList<Wine_WineVivino> wines = Mongo.getWineVivinoByFilter(field, value);
    if (wines == null || wines.isEmpty()) {
        System.out.println("Nessun WineVivino con price <= " + value);
    } else {
        System.out.println("Wines Vivino con price <= " + value);
        for (Wine_WineVivino w : wines) {
            System.out.println(w);
        }
    }
}
    //OK FUNZIONANTE
    @Test
    public void testGetWineMagByWineryName() {
    String wineryName = "Terre di Giurfo"; 

    ArrayList<Wine_WineMag> wines = Mongo.getWineMagByWineryName(wineryName);
    if (wines == null || wines.isEmpty()) {
        System.out.println("Nessun vino WineMag con cantina = " + wineryName);
    } else {
        System.out.println("Vini WineMag con cantina '" + wineryName + "':");
        for (Wine_WineMag w : wines) {
            System.out.println(w);
        }
    }
}

    //OK FUNZIONANTE
    @Test
    public void testGetWineVivinoByWineryName() {
    String wineryName = "M. Chapoutier";

    ArrayList<Wine_WineVivino> wines = Mongo.getWineVivinoByWineryName(wineryName);
    if (wines == null || wines.isEmpty()) {
        System.out.println("Nessun vino trovato con cantina = " + wineryName);
    } else {
        System.out.println("Vini di tipo Vivino aventi cantina '" + wineryName + "':");
        for (Wine_WineVivino w : wines) {
            System.out.println(w);
        }
    }
}

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

}
