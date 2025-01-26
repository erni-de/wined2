package it.unipi.wined;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import it.unipi.wined.bean.Order;
import it.unipi.wined.bean.OrderList;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.config.Driver_Config;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.io.FileReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WinedApplication {

    //FUNZIONE UTILE (ANCHE PER IL CLIENT) PER INSERIRE NUOVI ORDINI
    public static  void simulate() {
        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new Gson();
        Random random = new Random();
       
        //PARAMETERS -- change manually
        //Path to data
        String usersPath = "/home/donald/user.json";
        String winesPath = "/home/donald/wines.json";
        String reviewsPath = "/home/donald/reviews.json";
        
        //Number of drivers for neo4j
        int n_drivers = 10;
            //no of iterations for each action
        
        int ORDER_ITER = 1000;
        int FOLLOW_ITER = 4000;
        int LIKE_ITER = 10000;
        int REVIEW_ITER = 1000;
        
        
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
                        r_users = random.ints(2, 0, users.length).toArray();
                        Neo4jGraphInteractions.followUser(users[r_users[0]].getNickname(), users[r_users[1]].getNickname());
                        System.out.println("[" + i + "] " + "User " + users[r_users[0]].getNickname() + " follows " + users[r_users[1]].getNickname());
                }

                
                for (int i = 0; i < LIKE_ITER; i++) { //liking sim
                        r_users = random.ints(4, 0, users.length).toArray();
                        r_wines = random.ints(2, 0, wines.length).toArray();

                        Neo4jGraphInteractions.likeWine(wines[r_wines[0]].getName(), users[r_users[0]].getNickname());
                        System.out.println("[" + i + "] " + "User " + users[r_users[3]].getNickname() + " likes " + wines[r_wines[1]].getName());
                    
                }

                for (int i = 0; i < REVIEW_ITER; i++) { //reviews sim
                        r_users = random.ints(4, 0, users.length).toArray();
                        r_wines = random.ints(2, 0, wines.length).toArray();
                        r_rev = random.nextInt(reviews.length);
                        Neo4jGraphInteractions.insertReview(wines[r_wines[1]].getName(), reviews[r_rev], users[r_users[1]].getNickname());
                        System.out.println("[" + i + "] " + "User " + users[r_users[3]].getNickname() + " reviewes " + wines[r_wines[1]].getName());
                    
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        simulate();
        System.out.println("Ciao");
        SpringApplication.run(WinedApplication.class, args);

    }

}
