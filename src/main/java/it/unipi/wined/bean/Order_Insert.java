/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

/**
 *
 * @author nicol
 */
public class Order_Insert {
    private String wineId;
    private int price;
    
    public Order_Insert(String wineId, int price){
        this.wineId = wineId;
        this.price = price;
    }
    
    public String getWineId(){
        return wineId;
    }
    
    public int getPrice(){
        return price;
    }
    
    public void setWineId(String wineId){
        this.wineId = wineId;
    }
    
    public void setPrice(int price){
        this.price = price;
    }
    
}
