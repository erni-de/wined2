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
public class VivinoWine implements Serializable {
    public long id; //id of wine
    public String name; //name of wine
    public List<VivinoReview> reviews;
    public VivinoWinery winery;
    public VivinoRegion region;
    public String alcohol_percentage;
    public String price;
   
    
    public void printReviews(){
        for (VivinoReview r : reviews){
            System.out.println(r);
        }
    }
    
    public int alcoholPercentageAsInt(){
        return Integer.parseInt(alcohol_percentage.replace("%", ""));
    }
    
    public float priceAsFloat(){
        return Float.parseFloat(price.replace("%", ""));
    }
}
