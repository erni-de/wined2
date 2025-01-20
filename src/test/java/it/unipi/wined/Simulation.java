/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.connectionIp;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import java.io.FileReader;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
        Neo4JUtils.loadWine("/home/erni/Downloads/wines_definitive(1).json");
        Neo4JUtils.loadUsers("/home/erni/Downloads/users_final.json");
    }

    @Test
    public void simulate() {
        Gson gson = new Gson();
        Random random = new Random();
        
        
        

        //PARAMETERS -- change manually
            //Path to data
        String usersPath = "/home/erni/Downloads/users_final.json";
        String winesPath = "/home/erni/Downloads/wines_definitive(1).json";
        String reviewsPath = "/home/erni/Downloads/reviews/preprocessed_reviews.json";
            //Number of drivers
        int n_drivers = 10;
        
        
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

            User[] users = gson.fromJson(readerU, User[].class);
            Wine_WineMag[] wines = gson.fromJson(readerW, Wine_WineMag[].class);
            Review[] reviews = gson.fromJson(readerR, Review[].class);
            int[] r_users;
            int[] r_wines;
            int r_rev;
            while (true) {

                for (int i = 0; i < 10000; i++) { //following sim
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

                for (int i = 0; i < 30000; i++) { //liking sim
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

                for (int i = 0; i < 700; i++) { //reviews sim
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
        }

    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
