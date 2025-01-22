/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author erni
 */
public class StatsResponse implements Serializable{
    
    public int males;
    public int females;
    public HashMap<String, Integer> regions = new HashMap<String, Integer>();
    public HashMap<String, Integer> priceCategories = new HashMap<String, Integer>();
    public long uniqueWines;
    public double avgCost;
    public ArrayList<String> bestSelling = new ArrayList<>();
    
    public StatsResponse() {
        
    }

    public void setMales(int males) {
        this.males = males;
    }

    public void setFemales(int females) {
        this.females = females;
    }

    public void setUniqueWines(long uniqueWines) {
        this.uniqueWines = uniqueWines;
    }

    public void setAvgCost(double avgCost) {
        this.avgCost = avgCost;
    }
    
    
    
    
}
