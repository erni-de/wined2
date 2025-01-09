/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.wined.bean;

/**
 *
 * @author nicol
 */
public class PaymentInfo {
    
    private String cardNumber;
    private int CVV;
    private String expirationDate;
    
    //----------CONSTRUCTORS---------

    //Costruttore vuoto a volte se non lo metto si bugga
    public PaymentInfo(){
        
    }
    
    public PaymentInfo(String cardNumber, int CVV, String expirationDate){
       this.cardNumber = cardNumber;
       this.CVV = CVV;
       this.expirationDate = expirationDate;
    }

    //----------GET METHODS----------
    public String getCardNumber(){
        return cardNumber;
    }
    
    public int getCVV(){
        return CVV;
    }
    
    public String expirationDate(){
        return expirationDate;
    }
    
    //----------SET METHODS----------
    public void setCardNumber(String cardNumber){
        this.cardNumber = cardNumber;
    }
    
    public void setCVV(int CVV){
        this.CVV = CVV;
    }
    
    public void setExpirationDate(String expirationDate){
        this.expirationDate = expirationDate;
    }
    
    @Override
    public String toString() {
        return "CreditCard{" +
               "cardNumber='" + cardNumber + '\'' +
               ", CVV=" + CVV +
               ", expirationDate='" + expirationDate + '\'' +
               '}';
    }
    
}
