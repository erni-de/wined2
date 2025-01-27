/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.unipi.wined.bean.Order;
import it.unipi.wined.bean.OrderList;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class Simulation {
    
     @Test
    public void populateDB() {
        //Add Users and Wines node to graph 
        System.out.println("Carico i vini");
        Neo4JUtils.loadWine("/home/erni/Downloads/wines_final_V2.json");
        System.out.println("Carico gli user");
        Neo4JUtils.loadUsers("/home/erni/Downloads/user.json");
    }

    @Test
    public void simulate() {
        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new Gson();
        Random random = new Random();
       
        //PARAMETERS -- change manually
        //Path to data
        String usersPath = "/home/erni/Downloads/user.json";
        String winesPath = "/home/erni/Downloads/wines_final_V2.json";
        String reviewsPath = "/home/erni/Downloads/reviews/reviews.json";
        
        //Number of drivers for neo4j
        int n_drivers = 100;
            //no of iterations for each action
        
        int ORDER_ITER = 1000;
        int FOLLOW_ITER = 100;
        int LIKE_ITER = 300;
        int REVIEW_ITER = 70;
        
        
        Driver[] drivers = new Driver[n_drivers];

        //init drivers
        //IMPORTANT -- see NEO4JUtils class to set your own 
        // connection address, username and password for neo4j 
        for (int i = 0; i < n_drivers; i++) {
            drivers[i] = establishConnection();
        }

        try {
            FileReader readerU = new FileReader(usersPath);
            FileReader readerW = new FileReader(winesPath);
            FileReader readerR = new FileReader(reviewsPath);

            User[] users = mapper.readValue(readerU, User[].class);
            Wine_WineMag[] wines = mapper.readValue(readerW, Wine_WineMag[].class);
            Review[] reviews = mapper.readValue(readerR, Review[].class);
            int[] r_users;
            int[] r_wines;
            int r_rev;
            while (true) {
                
                //set max n of wines per order
                int n_wines = 7;
                for (int i = 0; i < ORDER_ITER; i++){
                    r_wines = random.ints(n_wines, 0, wines.length).toArray();
                    int r_user = random.nextInt(users.length);
                    ArrayList<OrderList> ordered = new ArrayList<>();
                    for (int j = 0; j < random.nextInt(1,n_wines); j++){
                        Wine_WineMag wine = wines[r_wines[j]];
                        ordered.add(new OrderList(wine.getId(), wine.getName(), wine.getPrice(), random.nextInt(1,4)));
                    }
                    long millis = ThreadLocalRandom.current().nextLong(Date.UTC(100, 0, 1, 0, 0, 0), Date.UTC(122, 0, 1, 0, 0, 0));
                    Order order = new Order(UUID.randomUUID().toString(), ordered, new Date(millis).toString(), new Date(millis + 86400000).toString());
                    Mongo.addWineOrder(users[r_user].getNickname(), order);
                    System.out.println("[" + i + "] " + "User " + users[r_user].getNickname() + " has made an order : " + order.toString());
                    System.out.println("Threads : " + Thread.activeCount());
                }
                
                for (int i = 0; i < FOLLOW_ITER; i++) { //following sim
                    for (Driver driver : drivers) {
                        r_users = random.ints(2, 0, users.length).toArray();
                        driver.executableQuery("""
                               MATCH (a:user {username: $selfUser})
                               MATCH (b:user {username: $targetUser})
                               CREATE (a)-[:FOLLOWS]->(b)
                               """).
                                withParameters(Map.of("selfUser", users[r_users[0]].getNickname(), "targetUser", users[r_users[1]].getNickname())).
                                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                                execute();
                        System.out.println("[" + i + "] " + "User " + users[r_users[0]].getNickname() + " follows " + users[r_users[1]].getNickname());
                    }
                }

                
                for (int i = 0; i < LIKE_ITER; i++) { //liking sim
                    for (Driver driver : drivers) {
                        r_users = random.ints(4, 0, users.length).toArray();
                        r_wines = random.ints(2, 0, wines.length).toArray();

                        driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})
                               MATCH (u:user {username: $userName})
                               CREATE (u)-[:LIKES]->(w)
                               """).
                                withParameters(Map.of("wineName", wines[r_wines[1]].getName(), "userName", users[r_users[3]].getNickname())).
                                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                                execute();
                        driver.executableQuery("""
                               MATCH (u:user)-[l:LIKES]->(w:wine {name: $wine})
                               WITH w, COUNT(l) as likes SET w.likes = likes
                               """).
                                withParameters(Map.of("wine", wines[r_wines[1]].getName())).
                                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                                execute();
                        System.out.println("[" + i + "] " + "User " + users[r_users[3]].getNickname() + " likes " + wines[r_wines[1]].getName());
                    }
                }

                for (int i = 0; i < REVIEW_ITER; i++) { //reviews sim
                    for (Driver driver : drivers) {
                        r_users = random.ints(4, 0, users.length).toArray();
                        r_wines = random.ints(2, 0, wines.length).toArray();
                        r_rev = random.nextInt(reviews.length);
                        driver.executableQuery("""
                               MATCH (a:wine {name: $wineName})
                               MATCH (c:user {username: $userName})
                               CREATE (b:review {title: $reviewTitle, text: $reviewCorpus, rating: $reviewRating})
                               CREATE (a)-[:REVIEWED]->(b)
                               CREATE (b)-[:WRITTEN_BY]->(c)
                               """).
                                withParameters(Map.of("wineName", wines[r_wines[1]].getName(), "reviewTitle", reviews[r_rev].title, "reviewCorpus", reviews[r_rev].body, "reviewRating", reviews[r_rev].rating, "userName", users[r_users[3]].getNickname())).
                                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                                execute();
                        EagerResult result = driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})-[:REVIEWED]->(r:review)
                               RETURN r.rating
                               """).
                                withParameters(Map.of("wineName", wines[r_wines[1]].getName())).
                                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                                execute();
                        String res = result.records().toString().replace("Record<", "").replace(">", "").replace("r.", "");
                        Review[] rev = gson.fromJson(res, Review[].class);
                        int totrev = 0;
                        float sumrev = 0;
                        for (Review r : rev) {
                            sumrev = sumrev + Float.parseFloat(r.rating);
                            totrev++;
                        }
                        float wineRating = sumrev / totrev;
                        driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})
                               SET w.rating = $wineRating
                               """).
                                withParameters(Map.of("wineName", wines[r_wines[1]].getName(), "wineRating", wineRating)).
                                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                                execute();
                        System.out.println("[" + i + "] " + "User " + users[r_users[3]].getNickname() + " reviewes " + wines[r_wines[1]].getName());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
