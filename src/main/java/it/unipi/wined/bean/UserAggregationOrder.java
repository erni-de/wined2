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
    
    private String userId;
    private int orderCount;
    private double totalSpent;
    
    public UserAggregationOrder(){
        
    }
    
    public UserAggregationOrder(String id, int orderCount, double totalSpent){
        this.userId = id;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
    }
    
    public String getUserId(){
        return userId;
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
                "userId='" + userId + '\'' +
                ", orderCount=" + orderCount +
                ", totalSpent=" + totalSpent +
                '}';
    }
}
