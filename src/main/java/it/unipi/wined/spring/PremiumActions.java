/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import it.unipi.wined.bean.User;
import it.unipi.wined.driver.Mongo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author erni
 */
@Controller
@RequestMapping(path = "/premium-act/")
public class PremiumActions {
    
    public static boolean isPremium(User user) {
        User.Level level = Mongo.RetrieveUserLevel(user.getNickname(), user.getPassword());
        return level == User.Level.PREMIUM || level == User.Level.ADMIN;
    }
    
    
    
}
