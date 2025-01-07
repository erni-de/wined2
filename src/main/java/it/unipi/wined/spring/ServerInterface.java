/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import it.unipi.wined.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author erni
 */
@Controller
@RequestMapping(path = "/actions/")
public class ServerInterface {
    
    public static User currentUser;
    
    @PostMapping(path = "/register")
    public @ResponseBody String registerUser(){
        //mathod that interacts with MongoDb to register a new user and add
        //that user's node in neo4j aswell.
        //user needs to pass a list of serialized strings for the registering
        // like FIRST_NAME, LAST_NAME, ADDRESS, Username, and a password
        
        //N.B. THIS WILL JUST REGISTER THE USER, WHO NEEDS TO REAUTHENTICATE
        //AGAIN TO ACTULLY LOGIN.
        
        //We also need to add here whether or not the user is trying to
        //subscribe as a premium user.
        return "pass";
    }
    
    @PostMapping(path = "/register")
    public @ResponseBody String login(){
        //mathod that interacts with MongoDb to retrieve the user that
        //matches a given username-password pair.
        //user needs to pass a list of serialized strings for the username
        //and a password
        return "pass";
    }
    
}
