package it.unipi.wined.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//Ho dovuto usare le proprietà di Jackson per rimappare i nomi, poiché sono diversi
//Dal JSON rispetto a quelli che ho nella classe
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentInfo {
    
    @JsonProperty("card_number")
    private String cardNumber;  

    @JsonProperty("CVV")
    private int CVV;            
    
    @JsonProperty("expire_date")
    private String expirationDate; 
    
    //----------COSTRUTTORI---------
    public PaymentInfo(){ }

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

    public String getExpirationDate(){
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
        return "PaymentInfo{" +
               "cardNumber='" + cardNumber + '\'' +
               ", CVV=" + CVV +
               ", expirationDate='" + expirationDate + '\'' +
               '}';
    }
}
