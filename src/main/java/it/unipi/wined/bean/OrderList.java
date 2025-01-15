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

//Decided to implement the orders into the collection of users with an emebedded method.
//The reason is for avoiding join with other documents and for make a fast retrieve of all the orders
//Made by a users. The main problem in this case is the limit of dimension made by MongoDB for a single document
//So we suppose that an order can be composed maximum by 20 wines; we make this assumption for avoiding
//The split of a document and in this case for loosing the performance with the read and write operations
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderList {

    private String wine_id;
    private String wine_name;
    private double price;
    private int wine_number;

    //Costruttore vuoto per Jackson
    public OrderList() {
    }

    //Costruttore
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