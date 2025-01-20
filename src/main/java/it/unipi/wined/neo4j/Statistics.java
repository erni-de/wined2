/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.neo4j;

import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class Statistics {
    
    public static void bestWinesFollowedUsers(String username){
        Driver driver = Neo4JUtils.establishConnection();
        EagerResult result = driver.executableQuery("""
                               MATCH (u:user {username: $userName})-[:FOLLOWS]->(f:user)
                               MATCH (f)-[:LIKES]->
                               RETURN r.rating
                               """).
                withParameters(Map.of("userName", username)).
                withConfig(QueryConfig.builder().withDatabase("neo4j").build()).
                execute();
    }
}
