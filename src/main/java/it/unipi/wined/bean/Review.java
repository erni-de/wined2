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
    public String Text;
    public float rating;
    public User user;

    public Review(String Text, float rating, User user) {
        this.Text = Text;
        this.rating = rating;
        this.user = user;
        id = id_count;
        id_count++;
    }
    
    public Review(VivinoReview vr, User user){
        this.Text = vr.text;
        this.rating = vr.rating;
        id = id_count;
        id_count++;
    }
}
