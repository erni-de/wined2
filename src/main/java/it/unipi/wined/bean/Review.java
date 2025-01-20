/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

import it.unipi.wined.bean.User;
import it.unipi.wined.json.objects.VivinoReview;

/**
 *
 * @author erni
 */
public class Review {
    public long id;
    public static long id_count;
    public String title;
    public String body;
    public String rating;
    public User user;

    public Review(){
        
    }
    
    public Review(String rating, String title, String body) {
        this.title = title;
        this.body = body;
        this.rating = rating;
    }
}
