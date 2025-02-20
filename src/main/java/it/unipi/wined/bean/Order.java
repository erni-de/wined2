/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 *
 * @author nicol
 */

//An order is implemented as an array of order_list, so in each order we have an
//Array of order_list that specifies the wine bought, the quantity and the price
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @JsonProperty("id_order")
    private String id_order;
    @JsonProperty("order_list")
    private ArrayList<OrderList> orderList;
    @JsonProperty("confirmation_date")
    private String confirmation_date;
    @JsonProperty("delivery_date")
    private String delivery_date;
    @JsonProperty("order_total_cost")
    private double order_total_cost;

    //---------------
    //--COSTRUTTORI--
    //---------------
    
    //I inizialize the array List for avoiding NullPointerException
    public Order() {
        this.order_total_cost = 0;
        this.orderList = new ArrayList<>();
    }

    public Order(String id_order, ArrayList<OrderList> orderList, String confirmation_date,
            String delivery_date) {

        //Recall the empty constructor for initializing
        this();
        this.id_order = id_order;
        this.orderList = orderList;
        this.confirmation_date = confirmation_date;
        this.delivery_date = delivery_date;

        for (int i = 0; i < orderList.size(); i++) {
            order_total_cost = order_total_cost + orderList.get(i).getPrice() * orderList.get(i).getWine_number();
        }

    }

    public Order(String id_order, ArrayList<OrderList> orderList, String confirmation_date,
            String departure_date, String delivery_date, double feedback,
            double order_total_cost) {

        this();
        this.id_order = id_order;
        this.orderList = orderList;
        this.confirmation_date = confirmation_date;
        this.delivery_date = delivery_date;
        this.order_total_cost = order_total_cost;
    }

    //---------------
    //------GET------
    //---------------
    
    public String getIdOrder() {
        return id_order;
    }

    public ArrayList<OrderList> getOrderElements() {
        return orderList;
    }

    public String getConfirmationDate() {
        return confirmation_date;
    }

    public String getDeliveryDate() {
        return delivery_date;
    }

    public double getOrderTotalCost() {
        return order_total_cost;
    }

    //---------------
    //------SET------
    //---------------
    public void setIdOrder(String id_order) {
        this.id_order = id_order;
    }

    public void setOrderElements(OrderList orderlist) {
        this.orderList.add(orderlist);
    }

    public void setConfirmationDate(String confirmation_date) {
        this.confirmation_date = confirmation_date;
    }

    public void setDeliveryDate(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public void setOrderTotalCost(double order_total_cost) {
        this.order_total_cost = order_total_cost;
    }

    @Override
    public String toString() {
        return "Order {"
                + "id_order=" + id_order
                + ", orderList=" + orderList.toString()
                + ", confirmation_date='" + confirmation_date + '\''
                + ", delivery_date='" + delivery_date + '\''
                + ", order_total_cost=" + order_total_cost
                + '}';
    }
}
