/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.json.objects;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author erni
 */
public class VivinoWrapper implements Serializable{
    long id;
    String name;
    double price;
    List<VivinoWine> wines;
    
    public List<VivinoWine> getWines(){
        return wines;
    }
    
}
