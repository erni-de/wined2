/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.google.gson.Gson;
import it.unipi.wined.bean.User;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import it.unipi.wined.spring.utils.LoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author erni
 */
@Controller
@RequestMapping(path = "/access/")
public class Access {

    public static User currentUser;

    @PostMapping(path = "/check-username")
    public @ResponseBody
    String checkUsername(@RequestBody String jsonUsername) {
        Gson gson = new Gson();
        String username = gson.fromJson(jsonUsername, String.class);
        if (Neo4jGraphInteractions.checkIfUserExists(username)) {
            return "1";//if user already exists
        } else {
            return "0";//if user doesn't exist
        }
    }

    /* 
     */
    @PostMapping(path = "/register")
    public @ResponseBody
    String registerUser(@RequestBody String register_data) {
        Gson gson = new Gson();
        User registering_user = gson.fromJson(register_data, User.class);
        System.out.println(registering_user.toString());
        try {
            Neo4jGraphInteractions.addUserNode(registering_user);
            //some mongodb query to load new registering user into db
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }

    /*
    Takes as input a json serialized LoginRequest object (see it.unipi.wined.spring.utils)
    From data (username - password pair) from LoginRequest, it matches a registered
    user on MongoDB databas and returns a json serialization of that user back to the client
    If no matching registered user is found an error message is returned
     */
    @PostMapping(path = "/login")
    public @ResponseBody
    String login(@RequestBody String jsonLoginRequest) {
        try {
            Gson gson = new Gson();
            LoginRequest lr = gson.fromJson(jsonLoginRequest, LoginRequest.class);
            //if user found on mongo
            //User retUser = new User(12, User.level.ADMIN, "jacksonnn", "jack", "jackson");
            return lr.username;
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
        //else
        //return "User not found";
    }

}
