/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.google.gson.Gson;
import it.unipi.wined.bean.AbstractWine;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author erni
 */
@Controller
@RequestMapping(path = "/regular-user-act/")
public class Actions {

    @PostMapping(path = "/follow")
    public @ResponseBody
    String followUser(@RequestBody String usersToRelate) {
        //method should invoke followUser method in it.unipi.wined.neo4j.interaction.GraphActions.java
        Gson gson = new Gson();
        String[] users = gson.fromJson(usersToRelate, String[].class);
        try {
            Neo4jGraphInteractions.followUser(users[0], users[1]);
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

    /**
     * Takes as input a String (target user username) and returns a String to be
     * decoded from the client service of the form
     * "[Record<{w.name: "wine_name"}>, Record<{w.name: "wine_name"}> ,..., ]";
     * Will return "[]" if the target user has made no reviews.
     *
     * @param username
     * @return String
     */
    @PostMapping(path = "/user-reviewed-wines")
    public @ResponseBody
    String checkUsersReviewedWines(@RequestBody String username) {
        return Neo4jGraphInteractions.usersReviewedWines(new Gson().fromJson(username, String.class));
    }

    @PostMapping(path = "/user-reviews")
    public @ResponseBody
    String userReviews(@RequestBody String username) {
        Gson gson = new Gson();
        List<org.neo4j.driver.Record> ret = Neo4jGraphInteractions.usersReviews(new Gson().fromJson(username, String.class));
        ArrayList<Review> reviews = new ArrayList<>();
        for (org.neo4j.driver.Record r : ret) {
            reviews.add(new Review(r.get("w.name") + "", r.get("b.rating") + "", r.get("b.text") + "", r.get("b.title") + ""));
        }
        return gson.toJson(reviews);
    }

    @PostMapping(path = "/wine-reviews")
    public @ResponseBody
    String wineReviews(@RequestBody String wine) {
        Gson gson = new Gson();
        List<org.neo4j.driver.Record> ret = Neo4jGraphInteractions.wineReviews(new Gson().fromJson(wine, String.class));
        ArrayList<Review> reviews = new ArrayList<>();
        for (org.neo4j.driver.Record r : ret) {
            reviews.add(new Review(wine, r.get("b.rating") + "", r.get("b.text") + "", r.get("b.title") + "", r.get("u.username") + ""));
        }
        return gson.toJson(reviews);
    }

    @PostMapping(path = "/check-wine")
    public @ResponseBody
    String checkWine(@RequestBody String jsonWinename) {
        Gson gson = new Gson();
        String winename = gson.fromJson(jsonWinename, String.class);
        if (Neo4jGraphInteractions.checkIfWineExists(winename)) {
            return "1";//if wine already exists
        } else {
            return "0";//if wine doesn't exist
        }
    }

    @PostMapping(path = "/review")
    public @ResponseBody
    String reviewWine(@RequestBody String inputRev) {
        //method should invoke followUser method in it.unipi.wined.neo4j.interaction.GraphActions.java
        Gson gson = new Gson();
        String[] review = gson.fromJson(inputRev, String[].class);

        String username = review[0];
        String wine = review[1];
        Review rev = new Review(review[1], review[2], review[3], review[4]);
        try {
            Neo4jGraphInteractions.insertReview(wine, rev, username);
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

    @PostMapping(path = "/like-wine")
    public @ResponseBody
    String likeWine(@RequestBody String input) {
        try {//method should invoke followUser method in it.unipi.wined.neo4j.interaction.GraphActions.java
            Gson gson = new Gson();
            String[] likeRel = gson.fromJson(input, String[].class);

            Neo4jGraphInteractions.likeWine(likeRel[1], likeRel[0]);
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }

    }

    @PostMapping(path = "/get-suggested-by-followers")
    public @ResponseBody
    String getSuggestedByFollowers(@RequestBody String input) {
        try {//method should invoke followUser method in it.unipi.wined.neo4j.interaction.GraphActions.java
            Gson gson = new Gson();
            User user = gson.fromJson(input, User.class);

            List<org.neo4j.driver.Record> records = Neo4jGraphInteractions.getSuggestedWines(user.getNickname());
            ArrayList<String> retList = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                retList.add(records.get(i).get("w.name") + "");
            }

            return gson.toJson(retList);
        } catch (Exception e) {
            e.printStackTrace();
            return "500";
        }

    }

    /**
     *Input should be a serialization of a user object a string containing 
     * the name of the wine to be returned.
     * @param input
     * @return
     */
    @PostMapping(path = "/get-wine")
    public @ResponseBody
    String getWinesResume(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            String wine_name = gson.fromJson(gson.toJson(par[1]), String.class);
            ArrayList<AbstractWine> wines = Mongo.getWineByName(wine_name);
            String serialized = gson.toJson(wines);
            if ((PremiumActions.isPremium(user))) {
                return serialized;
            } else {
                return gson.toJson(gson.fromJson(serialized, Wine_WineMag[].class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "500";
        }

    }

}
