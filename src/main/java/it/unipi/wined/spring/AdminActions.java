/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unipi.wined.spring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.unipi.wined.bean.AbstractWine;
import it.unipi.wined.bean.User;
import it.unipi.wined.bean.Wine_WineMag;
import it.unipi.wined.bean.Wine_WineVivino;
import it.unipi.wined.driver.Mongo;
import it.unipi.wined.neo4j.interaction.Neo4jGraphInteractions;
import it.unipi.wined.spring.utils.StatsResponse;
import java.util.ArrayList;
import java.util.UUID;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author erni
 */
@Controller
@RequestMapping(path = "/admin-act/")
public class AdminActions {

    public static boolean isAdmin(User user) {
        return Mongo.RetrieveUserLevel(user.getNickname(), user.getPassword()) == User.Level.ADMIN;
    }

    public static boolean isAdmin(String nickname, String password) {
        return Mongo.RetrieveUserLevel(nickname, password) == User.Level.ADMIN;
    }
   
    
    /**
     * Need a serialization of an array list o fthe user performing the action
     * and an id of a vivino wine
     * @param input
     * @return 
     */
    @PostMapping(path = "/add-wine-vivino")
    public @ResponseBody
    String addWineVivino(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            
            Wine_WineVivino wine = gson.fromJson(gson.toJson(par[1]), Wine_WineVivino.class);
            String id = Mongo.getWineryIdByName(wine.getWinery_name());
            if (id == null){
                id = UUID.randomUUID().toString();
            }    
            wine.setId(id);
            if (isAdmin(user)) {
                if (Mongo.addWine(wine)) {
                    try {
                        Neo4jGraphInteractions.addWine(wine.getName());
                    } catch (Exception e) {
                        Mongo.deleteWine(id);
                        e.printStackTrace();
                        return "401";
                    }
                    return "200";
                } else {
                    return "400";
                }
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }

    /**
     * Need a serialization of an array list of the user performing the action
     * and an id of a WineMag wine
     * @param input
     * @return 
     */
    @PostMapping(path = "/add-wine-mag")
    public @ResponseBody
    String addWineWineMag(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            
            Wine_WineMag wine = gson.fromJson(gson.toJson(par[1]), Wine_WineMag.class);
            System.out.println(input);
            System.out.println(wine.toString());

            String id = Mongo.getWineryIdByName(wine.getWinery_name());
            if (id == null){
                id = UUID.randomUUID().toString();
            }   
            wine.setWinery_id(id);
            wine.setId(UUID.randomUUID().toString());
            if (isAdmin(user)) {
                if (Mongo.addWine(wine)) {
                    try {
                        Neo4jGraphInteractions.addWine(wine.getName());
                    } catch (Exception e) {
                        Mongo.deleteWine(id);
                        e.printStackTrace();
                        return "401";
                    }
                    return "200";
                } else {
                    return "400";
                }
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }

    /**
     * Need a serialization of an array list of the user performing the action
     * and an id of a WineMag wine
     * @param input
     * @return 
     */
    @PostMapping(path = "/delete-wine")
    public @ResponseBody
    String deleteWine(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            Object[] par = gson.fromJson(input, Object[].class);
            User user = gson.fromJson(gson.toJson(par[0]), User.class);
            String name = gson.fromJson(gson.toJson(par[1]), String.class);
            AbstractWine wine;
            if(Mongo.getWineByName(name).get(0).getProvenance().equals("W")){
                wine = (Wine_WineMag) Mongo.getWineByName(name).get(0);
            } else {
                wine = (Wine_WineVivino) Mongo.getWineByName(name).get(0);
            }
            if (isAdmin(user)) {
                if (Mongo.deleteWine(wine.getId())) {
                    try {
                        Neo4jGraphInteractions.deleteWine(wine.getName());
                        return "200";
                    } catch (Exception e) {
                        Mongo.addWine(wine);
                        e.printStackTrace();
                        return "401";
                    }
                } else {
                    return "400";
                }
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }
    

    /**
     * Input should be a serialized list of two users; the first one should be
     * the user performing the action, and the second one the user to delete; It
     * will be checked that the first user is an admin
     *
     * @param input
     * @return
     */
    @PostMapping(path = "/delete")
    public @ResponseBody
    String deleteUser(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(input, User[].class);
            if (isAdmin(users[0])) {
                User user = Mongo.RetrieveUser(users[1].getNickname());
                //add filter lookup in mongo for user level
                if (Mongo.deleteUser(users[1].getNickname())) {
                    try {
                        Neo4jGraphInteractions.deleteUserNode(users[1]);
                    } catch (Exception e) {
                        Mongo.addUser(user);
                        e.printStackTrace();
                        return "400";
                    }
                    return "200";
                } else {
                    return "400";
                }
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }

    /**
     * Input should be a serialized list of two users; the first one should be
     * the user performing the action, and the second one the user to upgrade;
     * It will be checked that the first user is an admin
     *
     * @param input
     * @return
     */
    @PostMapping(path = "/update-to-admin")
    public @ResponseBody
    String updateToAdmin(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(input, User[].class);
            if (isAdmin(users[0])) {
                //add filter lookup in mongo for user level
                Mongo.updateUser(users[1].getNickname(), "user_level", "ADMIN");
                return "200";
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }

    /**
     * Input should be a serialized list of two users; the first one should be
     * the user performing the action, and the second one the user to upgrade;
     * It will be checked that the first user is an admin
     *
     * @param input
     * @return
     */
    @PostMapping(path = "/update-to-premium")
    public @ResponseBody
    String updateToPremium(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            User[] users = gson.fromJson(input, User[].class);
            if (isAdmin(users[0])) {
                //add filter lookup in mongo for user level
                Mongo.updateUser(users[1].getNickname(), "user_level", "PREMIUM");
                return "200";
            } else {
                return "503";
            }
        } catch (Exception e) {
            return "500";
        }
    }
  
    /**
     * Input should be the current user serialized object to certify that it is
     * an admin; the function will return a StatsResponse object
     * (it.unipi.wined.spring.utils) which contains all information regarding
     * the system statistics.
     *
     * @param input
     * @return
     */
    @PostMapping(path = "/sys-stats")
    public @ResponseBody
    String returnStats(@RequestBody String input) {
        try {
            Gson gson = new Gson();
            ObjectMapper mapper = new ObjectMapper();
            User user = gson.fromJson(input, User.class);
            if (isAdmin(user)) {

                StatsResponse sr = new StatsResponse();
                //gender stats
                ArrayList<Document> genderDocs = Mongo.getGenderDistribution();
                for (Document doc : genderDocs) {
                    JsonNode jsonNode = mapper.readTree(doc.toJson());
                    if (jsonNode.get("Gender").asText().equals("female")) {
                        sr.females = jsonNode.get("Total").asInt();
                    }
                    if (jsonNode.get("Gender").asText().equals("male")) {
                        sr.males = jsonNode.get("Total").asInt();
                    }
                }

                //region stats
                ArrayList<Document> regionDocs = Mongo.getRegionDistribution();
                for (Document doc : regionDocs) {
                    JsonNode jsonNode = mapper.readTree(doc.toJson());
                    sr.regions.put(jsonNode.get("Region").asText(), jsonNode.get("Total").asInt());
                }

                //price stats
                ArrayList<Document> priceDocs = Mongo.getPriceBuckets();
                for (Document doc : priceDocs) {
                    JsonNode jsonNode = mapper.readTree(doc.toJson());
                    sr.priceCategories.put(jsonNode.get("Fascia").asText(), jsonNode.get("Numero Vini").asInt());
                }

                //unique wines present
                sr.uniqueWines = Mongo.countUniqueWineNames();
                //average cost
                sr.avgCost = Mongo.getAvgOrderCost();

                //best selling wine of last month
                for (AbstractWine w : Mongo.getBestSellingWineOfTheMonth()) {
                    sr.bestSelling.add(w.getName());
                }

                return gson.toJson(sr);
            } else {
                return "503";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "500";
        }
    }
    

}
