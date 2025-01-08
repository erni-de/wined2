/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.google.gson.Gson;
import it.unipi.wined.bean.Review;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
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
    
    @PostMapping(path = "/review")
    public @ResponseBody
    String reviewWine(@RequestBody String usersToRelate) {
        //method should invoke followUser method in it.unipi.wined.neo4j.interaction.GraphActions.java
        Gson gson = new Gson();
        String[] review = gson.fromJson(usersToRelate, String[].class);
        
        String username = review[0];
        String wine = review[1];
        Review rev = new Review(review[2], review[3], review[4]);
        try {
            Neo4jGraphInteractions.insertReview(wine, rev, username);
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

}
