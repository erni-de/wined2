/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j.interaction;

import com.google.gson.Gson;
import it.unipi.wined.bean.Neo4jListWrapper;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class Neo4jGraphInteractions {

    public static void recomputeRating(String wine) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})-[:REVIEWED]->(r:review)
                               RETURN r.rating
                               """).
                withParameters(Map.of("wineName", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        String res = result.records().toString().replace("Record<", "").replace(">", "").replace("r.", "");
        Gson gson = new Gson();

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
                withParameters(Map.of("wineName", wine, "wineRating", wineRating)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
    }

    /*
    Inserts review to a specific wine, from the current user
     */
    public static void insertReview(String wine, Review review, String username) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (a:wine {name: $wineName})
                               MATCH (c:user {username: $userName})
                               CREATE (b:review {title: $reviewTitle, text: $reviewCorpus, rating: $reviewRating})
                               CREATE (a)-[:REVIEWED]->(b)
                               CREATE (b)-[:WRITTEN_BY]->(c)
                               """).
                withParameters(Map.of("wineName", wine, "reviewTitle", review.title, "reviewCorpus", review.body, "reviewRating", review.rating, "userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        recomputeRating(wine); //to update the qine rating
    }

    public static void updateLikeCount(String wine) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (u:user)-[l:LIKES]->(w:wine {name: $wine})
                               WITH w, COUNT(l) as likes SET w.likes = likes
                               """).
                withParameters(Map.of("wine", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
    }

    public static void likeWine(String wine, String username) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (w:wine {name: $wineName})
                               MATCH (u:user {username: $userName})
                               CREATE (u)-[:LIKES]->(w)
                               """).
                withParameters(Map.of("wineName", wine, "userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        updateLikeCount(wine);
    }

    /*
    Inserts new user into graph (called when registering)
     */
    public static void addUserNode(User userToAdd) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               CREATE(u:user {id: $userId, username: $userName}) 
                               """).
                withParameters(Map.of("userId", userToAdd.id, "userName", userToAdd.getNickname())).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
    }

    public static void deleteUserNode(User userToDelete) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH(u:user {username: $userName})
                               DETACH DELETE u
                               """).
                withParameters(Map.of("userName", userToDelete.getNickname())).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
    }

    /*
    Create a follow relationship from the current user to a selected (target)
    user.
     */
    public static void followUser(String selfUsername, String targetUserName) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        driver.executableQuery("""
                               MATCH (a:user {username: $selfUser})
                               MATCH (b:user {username: $targetUser})
                               CREATE (a)-[:FOLLOWS]->(b)
                               """).
                withParameters(Map.of("selfUser", selfUsername, "targetUser", targetUserName)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
    }

    public static boolean checkIfUserExists(String username) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (n:user {username: $username})
                                RETURN CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END AS exists
                               """).
                withParameters(Map.of("username", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        return result.records().toString().equals("[Record<{exists: 1}>]");
    }

    public static boolean checkIfWineExists(String wine) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (n:wine {name: $winename})
                                RETURN CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END AS exists
                               """).
                withParameters(Map.of("winename", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        return !result.records().toString().equals("[]");
    }

    public static String usersReviewedWines(String user) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (w:wine)-[r:REVIEWED]->(b)-[d:WRITTEN_BY]->(u:user {username: $username})
                                RETURN w.name
                               """).
                withParameters(Map.of("username", user)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        return result.records().toString();
    }

    public static List<org.neo4j.driver.Record> usersReviews(String user) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (w:wine)-[r:REVIEWED]->(b)-[d:WRITTEN_BY]->(u:user {username: $username})
                                RETURN w.name, b.rating, b.text, b.title
                               """).
                withParameters(Map.of("username", user)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        return result.records();
    }

    public static List<org.neo4j.driver.Record> wineReviews(String wine) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        EagerResult result = driver.executableQuery("""
                                MATCH (w:wine {name : $wineName})-[r:REVIEWED]->(b)-[d:WRITTEN_BY]->(u:user)
                                RETURN b.rating, b.text, b.title, u.username
                               """).
                withParameters(Map.of("wineName", wine)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        driver.close();
        return result.records();
    }

    public static List<org.neo4j.driver.Record> getSuggestedWines(String username) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        var liked = driver.executableQuery("""
                                         MATCH (u:user {username : $userName})-[f:FOLLOWS]->(b:user)-[l:LIKES]->(w:wine)    
                                         WHERE NOT (w)<-[:LIKES]-(u)                                    
                                         RETURN w.name, w.rating, w.likes
                                         ORDER BY w.rating IS NULL , w.rating DESC, w.likes DESC
                                        """).
                withParameters(Map.of("userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        /*for (org.neo4j.driver.Record r : liked.records()){
            System.out.println(r.get("w.rating") + " " + r.get("w.likes") + " " + r.get("w.name"));
        }*/

        driver.close();
        return liked.records();
    }

    public static List<org.neo4j.driver.Record> getSuggestedWinesByFilter(String username, ArrayList<String> filteredWines) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        var liked = driver.executableQuery("""
                                         MATCH (w:wine)
                                         WHERE w.name IN $wineNames
                                         RETURN w.name
                                        """).
                withParameters(Map.of("wineNames", filteredWines)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
        /*for (org.neo4j.driver.Record r : liked.records()){
            System.out.println(r.get("w.rating") + " " + r.get("w.likes") + " " + r.get("w.name"));
        }*/

        driver.close();
        return liked.records();
    }

    public static ArrayList<String> getSuggestedUsers(String username, int size) {
        Driver driver = establishConnection(); //use ifconfig to retrive private ip
        boolean done = false;
        int distance = 2;
        ArrayList<String> ret = new ArrayList<>();
        while (!done) {
            var users = driver.executableQuery(
                    "MATCH (u:user {username: $userName})-[r:FOLLOWS * " + distance + " ]->(w:user) "
                    + """
                                        MATCH (u:user {username: $userName})-[:LIKES]->(s:wine)<-[:LIKES]-(w:user)
                                        ORDER BY s.rating IS NULL, s.rating DESC
                                        RETURN DISTINCT w.username 
                                        """
            ).
                    withParameters(Map.of("userName", username)).
                    withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                    execute();
            for (org.neo4j.driver.Record r : users.records()) {
                ret.add(r.get("w.username") + "");
                if (ret.size() == size) {
                    done = true;
                    break;
                }
            }
            distance++;
        }
        driver.close();
        return ret;
    }

}
