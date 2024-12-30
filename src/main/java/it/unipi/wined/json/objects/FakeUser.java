/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.json.objects;

import it.unipi.wined.User;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author erni
 */
public class FakeUser implements Serializable{
    public transient long id;
    public String firstname;
    public String lastname;
    public String email;
    public String phone;
    public String gender;
    public FakeAddress address;
    public String website;
    public String nickname;
    public String password;

    public FakeUser() {
        id = User.id_count;
        User.id_count++;
    }
    
    
    
    public static void printUsers(List<FakeUser> fuList){
        for(FakeUser fu : fuList){
            System.out.println(fu.nickname);
        }
    }
}
