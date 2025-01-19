/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.wined.bean;

/**
 *
 * @author nicol
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


//Classe astratta con i campi comuni a WineMag e Vivino
 

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractWine {
    
    @JsonProperty("_id")
    protected String id;
    protected String name;
    protected int price;
    protected String description;
    protected String country;
    protected String region;
    protected String provenance;
    protected String variety;
    protected int alcohol_percentage;

    public AbstractWine() {
    }

    public AbstractWine(String id, String name, int price, String description, 
                        String country, String region, String provenance, 
                        String variety, int alcohol_percentage) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.country = country;
        this.region = region;
        this.provenance = provenance;
        this.variety = variety;
        this.alcohol_percentage = alcohol_percentage;
    }

    public String getId() {
        return id;
    }

    public void setId(Object idObj) {
        if (idObj != null) {
            this.id = String.valueOf(idObj);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public int getAlcohol_percentage() {
        return alcohol_percentage;
    }

    public void setAlcohol_percentage(int alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
    }
}


