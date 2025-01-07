/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

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
    public @ResponseBody String followUser(@RequestBody String userEncoding){
        //method should invoke followUser method in it.unipi.wined.neo4j.interaction.GraphActions.java
        
        return "pass";
    }
    
}
