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

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderList {

    private String wine_id;
    private String wine_name;
    private double price;
    private int wine_number;

    //Costruttore vuoto per Jackson (senza si bugga)
    public OrderList() {
    }

    public OrderList(String wine_id, String wine_name, double price, int wine_number) {
        this.wine_id = wine_id;
        this.wine_name = wine_name;
        this.price = price;
        this.wine_number = wine_number;
    }

    //Getter & Setter
    public String getWine_id() {
        return wine_id;
    }
    public void setWine_id(String wine_id) {
        this.wine_id = wine_id;
    }

    public String getWine_name() {
        return wine_name;
    }
    public void setWine_name(String wine_name) {
        this.wine_name = wine_name;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getWine_number() {
        return wine_number;
    }
    public void setWine_number(int wine_number) {
        this.wine_number = wine_number;
    }

    @Override
    public String toString() {
        return "OrderList{" +
                "wine_id='" + wine_id + '\'' +
                ", wine_name='" + wine_name + '\'' +
                ", price=" + price +
                ", wine_number=" + wine_number +
                '}';
    }
}