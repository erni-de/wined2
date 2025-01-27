/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package it.unipi.wined;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.unipi.wined.bean.AbstractWine;
import it.unipi.wined.bean.Review;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.bean.UserAggregationOrder;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.bean.Neo4jListWrapper;
import it.unipi.wined.neo4j.Neo4JUtils;
import static it.unipi.wined.neo4j.Neo4JUtils.establishConnection;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.neo4j.driver.EagerResult;
import org.neo4j.driver.QueryConfig;

/**
 *
 * @author erni
 */
public class WinedTests {
    
}
