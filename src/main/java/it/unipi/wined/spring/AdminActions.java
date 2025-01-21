/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.google.gson.Gson;
import it.unipi.wined.bean.User;
import it.unipi.wined.driver.Mongo;
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
@RequestMapping(path = "/admin-act/")
public class AdminActions {

    public static boolean isAdmin(User user) {
        return Mongo.RetrieveUserLevel(user.getNickname(), user.getPassword()) == User.Level.ADMIN;
    }

    @PostMapping(path = "/delete")
    public @ResponseBody
    String deleteUser(@RequestBody String userToDelete) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(userToDelete, User[].class);
            if (isAdmin(users[0])) {
                //add filter lookup in mongo for user level
                if (Mongo.deleteUser(users[1].getNickname())) {
                    Neo4jGraphInteractions.deleteUserNode(users[1]);
                    return "200";
                } else {
                    return "400";
                }
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }
    
    @PostMapping(path = "/update-to-admin")
    public @ResponseBody
    String updateToAdmin(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(input, User[].class);
            if (isAdmin(users[0])) {
                //add filter lookup in mongo for user level
                Mongo.updateUser(users[1].getNickname(), "user_level", "ADMIN");
                return "200";
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }
    
    @PostMapping(path = "/update-to-premium")
    public @ResponseBody
    String updateToPremium(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(input, User[].class);
            if (isAdmin(users[0])) {
                //add filter lookup in mongo for user level
                Mongo.updateUser(users[1].getNickname(), "user_level", "PREMIUM");
                return "200";
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }

    //inser wine
    //delete wine
    //show stats
}
