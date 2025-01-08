/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j;

import it.unipi.wined.json.objects.VivinoWine;
import it.unipi.wined.json.objects.VivinoWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.json.objects.FakeUser;
import it.unipi.wined.json.objects.FakeUserWrapper;
import it.unipi.wined.json.objects.WinemagWine;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Stream;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class Neo4JUtils {
    
    public static String connectionIp = "192.168.244.146";
    
    public static Driver establishConnection(String uri, String user, String password) {
        return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
    
    public static List<FakeUser> getUsers(String filepath){
        Gson gson = new Gson();
        List<FakeUser> fu = new Stack<>();
        try {
            FileReader reader = new FileReader(filepath);
            FakeUserWrapper overall = gson.fromJson(reader, FakeUserWrapper.class);
            fu = overall.getUsers();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fu;
    }
    
    public static List<FakeUser> getUsers(String filepath1, String filepath2){
        return Stream.concat(getUsers(filepath1).stream(), getUsers(filepath2).stream()).toList();
    }
    
}
