/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.bean;

import it.unipi.wined.json.objects.FakeUser;
import java.util.Arrays;

/**
 *
 * @author erni
 */
public class User {

    public enum level {
        REGULAR, PREMIUM, ADMIN
    };
    //Admin have admin, premium and regular user privileges
    //Premium have premium and regular user privileges
    //Regular have just regular user privileges

    public long id;
    private level user_level;
    public static long id_count;
    public String firstname;
    public String lastname;

    public User(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
        id = id_count;
        id_count++;
    }

    public User(FakeUser fu) {
        this.firstname = fu.firstname;
        this.lastname = fu.lastname;
        id = id_count;
        id_count++;
    }

    public void setLevel(level l) {
        user_level = l;
    }

    public level setLevel() {
        return user_level;
    }

    public boolean[] getPrivileges() {
        if (user_level == null) {
            return new boolean[]{false, false, false};
        } else {
            switch (user_level) {
                case REGULAR:
                    return new boolean[]{true, false, false};
                case PREMIUM:
                    return new boolean[]{true, true, false};
                case ADMIN:
                    return new boolean[]{true, true, true};
            }
            return new boolean[]{false, false, false};
        }
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", user_level=" + user_level + ", firstname=" + firstname + ", lastname=" + lastname + ", " + Arrays.toString(getPrivileges()) + "}";
    }

}
