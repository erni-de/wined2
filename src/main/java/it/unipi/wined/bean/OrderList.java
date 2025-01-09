/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.wined.bean;

/**
 *
 * @author nicol
 */

//Decided to implement the orders into the collection of users with an emebedded method.
//The reason is for avoiding join with other documents and for make a fast retrieve of all the orders
//Made by a users. The main problem in this case is the limit of dimension made by MongoDB for a single document
//So we suppose that an order can be composed maximum by 20 wines; we make this assumption for avoiding
//The split of a document and in this case for loosing the performance with the read and write operations
public class OrderList {
    
    private String wine_id;
    private String wine_name;
    private double price;
    private int wine_number;
    
    //Costruttore principale ne abbiamo solo uno in questo caso
    //(Non quello vuoto per Jackson qui non si usa)
    private OrderList(String wine_id, String wine_name, double price, int wine_number){
        this.wine_id = wine_id;
        this.wine_name = wine_name;
        this.price = price;
        this.wine_number = wine_number;
    }
    
    //---------------
    //GETTER METHODS
    //---------------
    public String getWine_id(){
        return wine_id;
    }
    
    public String getWine_name(){
        return wine_name;
    }
    
    public double getPrice(){
        return price;
    }
    
    public int getWineNumber(){
        return wine_number;
    }
    
    //---------------
    //SETTER METHODS
    //--------------- 
    public void setWine_id(String wine_id){
        this.wine_id = wine_id;
    }
    
    public void setWine_name(String wine_name){
        this.wine_name = wine_name;
    }
    
    public void setPrice(double price){
        this.price = price;
    }
    
    public void setWineNumber(int wine_number){
        this.wine_number = wine_number;
    }
    
    @Override
    public String toString(){
        return "Order_Element{" + "wine_id = " + wine_id + "wine_name = "
                + "" + wine_name + "price = " + price 
                + "wine_number = " + wine_number + " }";
    }
}
