/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author erni
 */
public class Neo4jListWrapper implements Serializable{
   
    public ArrayList<String> wineNames;

    public Neo4jListWrapper(ArrayList<String> wineNames) {
        this.wineNames = wineNames;
    }

    
    
    public void setWineNames(ArrayList<String> wineNames) {
        this.wineNames = wineNames;
    }
    
    
    
}
