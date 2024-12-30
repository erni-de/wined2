/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.json.objects;

import java.io.Serializable;

/**
 *
 * @author erni
 */
public class VivinoReview implements Serializable{
    long id;
    public float rating;
    public String text;
    public String created_at;
}
