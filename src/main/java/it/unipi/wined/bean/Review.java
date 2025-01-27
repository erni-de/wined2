/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

import it.unipi.wined.bean.User;

/**
 *
 * @author erni
 */
public class Review {
    public String wine;
    public String rating;
    public String body;
    public String title;
    public String user;

    public Review(){
        
    }

    public Review(String wine, String rating, String body, String title, String user) {
        this.wine = wine;
        this.rating = rating;
        this.body = body;
        this.title = title;
        this.user = user;
    }
    
    
    public Review(String wine, String rating, String title, String body) {
        this.wine = wine;
        this.title = title;
        this.body = body;
        this.rating = rating;
    }
}
