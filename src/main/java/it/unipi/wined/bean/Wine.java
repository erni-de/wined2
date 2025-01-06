/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.wined.bean;

//Class import
//Using jackson for unpacking nested objects in JSON
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nicol
 */
//We decided to do a unique collection, using the characteristics of MongoDB. In fact this collection
//Stores different field, so a document can have different attributes, related to the fact that a wine
//Scraped from vivino has more fields than a wine scraped from winemag
public class Wine {
    
    //Those are all the attributes that aren't related to nested objects
    private String id;
    private String title;
    private String description;
    private String taster_name;
    private String taster_twitter_handle;
    private Integer price;
    private String designation;
    private String variety;
    private String region_1;
    private String region_2;
    private String province;
    private String country;
    private String alcohol_percentage;
    private String provenance;
    
    //The following attributes are related to nested objects
    //And they will be unpacked with Jackson
    
    //Winery
    private String winery_id;
    private String winery_name;
    
    //Taste nested objects (only in Vivino's wines)
    private Double acidity;
    private Double fizziness;
    private Double intensity;
    private Double sweetness;
    private Double tannin;
    private Integer user_structure_count;
    
    //Style nested objects (only in Vivino's wines)
    private Integer body;
    private String body_description;
    
}
