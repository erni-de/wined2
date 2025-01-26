/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

/**
 *
 * @author nicol
 */

public class UserAggregationOrder {
    
    private String nickname;
    private int orderCount;
    private double totalSpent;
    
    public UserAggregationOrder(){
        
    }
    
    public UserAggregationOrder(String id, int orderCount, double totalSpent){
        this.nickname = id;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
    }
    
    public String getUserId(){
        return nickname;
    }
    
    public int getOrderCount(){
        return orderCount;
    }
    
    public double getTotalSpent(){
        return totalSpent;
    }
    
     @Override
    public String toString() {
        return "UserOrderAggregationResult{" +
                "nickname='" + nickname + '\'' +
                ", orderCount=" + orderCount +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
