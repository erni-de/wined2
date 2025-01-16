/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.google.gson.Gson;
import it.unipi.wined.bean.User;
import it.unipi.wined.driver.Mongo;
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
    
    @PostMapping(path = "/delete")
    public @ResponseBody
    String deleteUser(@RequestBody String userToDelete) {
        try {
            Gson gson = new Gson();
            //add filter lookup in mongo for user level
            if (Mongo.deleteUser(gson.fromJson(userToDelete, User.class).getNickname())) {
                return "200";
            } else {
                return "400";
            }
        } catch (Exception e) {
            return "500";
        }
    }
}
