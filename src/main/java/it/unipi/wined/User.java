/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined;

import it.unipi.wined.json.objects.FakeUser;

/**
 *
 * @author erni
 */
public class User {
    public long id;
    public static long id_count;
    public String firstname;
    public String lastname;

    public User(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
        id = id_count;
        id_count++;
    }
    
    public User(FakeUser fu){
        this.firstname = fu.firstname;
        this.lastname = fu.lastname;
        id = id_count;
        id_count++;
    }
}
