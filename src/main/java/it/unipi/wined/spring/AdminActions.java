/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.google.gson.Gson;
import it.unipi.wined.bean.AbstractWine;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
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
    
    public static boolean isAdmin(String nickname, String password) {
        return Mongo.RetrieveUserLevel(nickname, password) == User.Level.ADMIN;
    }

    /**
     * Input should be a serialized list of two users; the first one should be the user
     * performing the action, and the second one the user to delete;
     * It will be checked that the first user is an admin
     * @param input
     * @return 
     */
    @PostMapping(path = "/delete")
    public @ResponseBody
    String deleteUser(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(input, User[].class);
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
    
    /**
     * Input should be a serialized list of two users; the first one should be the user
     * performing the action, and the second one the user to upgrade;
     * It will be checked that the first user is an admin
     * @param input
     * @return 
     */
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
    
    /**
     * Input should be a serialized list of two users; the first one should be the user
     * performing the action, and the second one the user to upgrade;
     * It will be checked that the first user is an admin
     * @param input
     * @return 
     */
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

    /**
     * Input should be a serialization of two Object.class objects;
     * The fist one represents the User performing the action that should be
     * checked to be an admin, the second one should be a representation of the
     * vivino wine to be added
     * 
     * @param input
     * @return 
     */
    @PostMapping(path = "/add-wine-vivino")
    public @ResponseBody
    String addVivinoWine(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            Wine_WineVivino wine = gson.fromJson(gson.toJson(par[1]), Wine_WineVivino.class);
            
            if(isAdmin(user)){
                Mongo.addWine(wine);
                return "200";
            } else {
                return "503";
            }   
        } catch (Exception e) {
            return "500";
        }
    }
    
    /**
     * Input should be a serialization of two Object.class objects;
     * The fist one represents the User performing the action that should be
     * checked to be an admin, the second one should be a representation of the
     * winemag wine to be added
     * 
     * @param input
     * @return 
     */
    @PostMapping(path = "/add-wine-winemag")
    public @ResponseBody
    String addWinemagWine(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            Wine_WineMag wine = gson.fromJson(gson.toJson(par[1]), Wine_WineMag.class);
            
            if(isAdmin(user)){
                Mongo.addWine(wine);
                return "200";
            } else {
                return "503";
            }   
        } catch (Exception e) {
            return "500";
        }
    }
    
    /**
     * Input should be a serialization of two Object.class objects;
     * The fist one represents the User performing the action that should be
     * checked to be an admin, the second one should be a String with the id
     * of the wine to delete
     * 
     * @param input
     * @return 
     */
    @PostMapping(path = "/add-wine-delete")
    public @ResponseBody
    String deleteWine(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            String id = gson.fromJson(gson.toJson(par[1]), String.class);
            
            if(isAdmin(user)){
                Mongo.deleteWine(id);
                return "200";
            } else {
                return "503";
            }   
        } catch (Exception e) {
            return "500";
        }
    }
    
    
    //show stats
}
