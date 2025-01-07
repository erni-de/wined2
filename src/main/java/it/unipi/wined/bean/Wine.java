/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.unipi.wined.bean;

//Class import
//Using jackson for unpacking nested objects in JSON
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nicol
 */
//We decided to do a unique collection, using the characteristics of MongoDB. In fact this collection
//Stores different field, so a document can have different attributes, related to the fact that a wine
//Scraped from vivino has more fields than a wine scraped from winemag
public class Wine {
    
    //CAMPI TOP-LEVEL OVVERO QUELLI DOVE NON SERVE L'UNPACKING CON JACKSON

    private String id;                  // "id" (stringa in WineMag, int in Vivino -> convertito a string)
    private String name;                // "name" 
    private int price;                  // "price" 
    private String variety;             // "variety" (Tipo di vino)
    private String region;              // "region" 
    private String description;         // "description" (potrebbe essere null)
    private String country;             // "country"
    private String alcohol_percentage;  // "alcohol_percentage"
    private String provenance;          // "provenance" -> "V" o "W" (Vivino o Winemag)

    //Campi propri di WineMag (null se il doc viene da Vivino)
    private String points;              
    private String taster_name;         
    private String province;            

    //Questi sono i campi annidati quindi quelli dove serve l'unpacking con Jackson
    
    // =====================
    //WINERY
    // =====================
    private String winery_id;
    private String winery_name;

    // =====================
    // TASTE (oggetto annidato solo di Vivino)
    // =====================
    private Double acidity;
    private Double fizziness;
    private Double intensity;
    private Double sweetness;
    private Double tannin;
    private Integer user_structure_count;

    private List<Flavor> flavorList; // "Array dei flavor quindi dei sapori che sente la gente"
    
    // =====================
    // STYLE (oggetto annidato, tipico di Vivino)
    // =====================
    private Integer body;
    private String body_description;
    private List<Food> foodList;    // "food" array


    // =====================
    // COSTRUTTORI
    // =====================
    
    //Ho pensato questi casi per i costruttori non so se ne serviranno altri

    //Questo è il costruttore che richiamo con this(); per evitare di inizializzare
    //Ogni volta nel codice
    public Wine() {
        this.flavorList = new ArrayList<>();
        this.foodList = new ArrayList<>();
    }

    public Wine(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public Wine(String id, String name, int price, String winery_name) {
        this();
        this.id = id;
        this.name = name;
        this.price = price;
        this.winery_name = winery_name;
    }

    public Wine(String id, String name, int price) {
        this();
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    //COSTRUTTORE COMPLETO
    //(Da decidere poi come si gestisce in questo caso il tipo dell'attributo a seconda dei valori
    //(Che mancano)
    public Wine(String id, String name, int price, String variety, String region, String description, 
            String country, String alcohol_percentage, String provenance, String points, String taster_name,
            String province, String winery_id, String winery_name, Double acidity, Double fizziness, Double intensity,
            Double sweetness, double tannin, Integer user_structure_count, List<Flavor> flavorList, Integer body,
            String body_description, List<Food> foodList){
    
        //Verifico se le liste sono NULL nel caso
        //Creo la lista vuota sennò fa casino con NULL Pointer
        if (flavorList == null){
            this.flavorList = new ArrayList<>();  
        }else{
            this.flavorList = flavorList;
        }
        
        if (foodList == null){
            this.foodList = new ArrayList<>();
        }else{
            this.foodList = foodList;
        }
        
        //Converto l'id a string per evitare confusioni (potrebbe arrivare integer)
        //Se il vino è di vivino non si sa come lo mette l'utente (in generale mongo da Integer)
        this.id = id;
        this.name = name;
        this.price = price;
        this.variety = variety;
        this.region = region;
        this.description = description;
        this.country = country;
        this.alcohol_percentage = alcohol_percentage;
        this.provenance = provenance;
        this.points = points;
        this.taster_name = taster_name;
        this.province = province;
        this.winery_id = winery_id;
        this.winery_name = winery_name;
        this.acidity = acidity;
        this.fizziness = fizziness;
        this.intensity = intensity;
        this.sweetness = sweetness;
        this.tannin = tannin;
        this.user_structure_count = user_structure_count;
        this.body = body;
        this.body_description = body_description;
    }
    
    // =====================
    // GETTER & SETTER
    // =====================

    public String getId() {
        return id;
    }

    /**
     * Se da MongoDB arriva un vino di Vivino, allora arriverà
     * un intero (es. 1153863), Jackson proverà ad assegnarlo a "id".
     * Per unificare, lo trasformo in String con "String.valueOf(...)".
     * Unifichiamo perché l'id di winemag è un'alfa-numerico (Python non dava integer)
     */
    
    public void setId(Object idObj) {
        if (idObj != null) {
            this.id = String.valueOf(idObj);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAlcohol_percentage() {
        return alcohol_percentage;
    }

    public void setAlcohol_percentage(String alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTaster_name() {
        return taster_name;
    }

    public void setTaster_name(String taster_name) {
        this.taster_name = taster_name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getWinery_id() {
        return winery_id;
    }

    public String getWinery_name() {
        return winery_name;
    }
    
    public Double getAcidity() {
        return acidity;
    }

    public Double getFizziness() {
        return fizziness;
    }

    public Double getIntensity() {
        return intensity;
    }

    public Double getSweetness() {
        return sweetness;
    }

    public Double getTannin() {
        return tannin;
    }

    public Integer getUser_structure_count() {
        return user_structure_count;
    }

    public List<Flavor> getFlavorList() {
        return flavorList;
    }
    
    public Integer getBody() {
        return body;
    }

    public String getBody_description() {
        return body_description;
    }

    public List<Food> getFoodList() {
        return foodList;
    }
    
    // ============================================
    // OGGETTI NESTED QUELLI DOVE SERVE L'UNPACKING
    // ============================================
    
    // ======================
    // Nested: Winery
    // ======================
    /**
     */
    @SuppressWarnings("unchecked")
    @JsonProperty("winery")
    private void unpackNestedWinery(Map<String, Object> winery) {
        if (winery != null) {
            //L'id può essere stringa (WineMag) o int (Vivino).
            Object wId = winery.get("id");
            if (wId != null) {
                //Lo salvo string per unificare
                this.winery_id = String.valueOf(wId);
            }
            this.winery_name = (String) winery.get("name");
        }
    }

    // ======================
    // Nested: Taste
    // ======================
    @SuppressWarnings("unchecked")
    @JsonProperty("taste")
    private void unpackNestedTaste(Map<String, Object> taste) {
        if (taste == null) return;

        //Prendo la structure che è annidata dentro taste in Vivino
        Map<String, Object> structure = (Map<String, Object>) taste.get("structure");
        if (structure != null) {
            this.acidity = toDouble(structure.get("acidity"));
            this.fizziness = toDouble(structure.get("fizziness"));
            this.intensity = toDouble(structure.get("intensity"));
            this.sweetness = toDouble(structure.get("sweetness"));
            this.tannin = toDouble(structure.get("tannin"));
            this.user_structure_count = toInteger(structure.get("user_structure_count"));
        }

        //Flavor
        List<Map<String, Object>> flavorArray = (List<Map<String, Object>>) taste.get("flavor");
        if (flavorArray != null) {
            List<Flavor> tmpFlavorList = new ArrayList<>();
            for (Map<String, Object> item : flavorArray) {
                Flavor fl = new Flavor();
                fl.setGroup((String) item.get("group"));

                Map<String, Object> statsMap = (Map<String, Object>) item.get("stats");
                if (statsMap != null) {
                    FlavorStats fs = new FlavorStats();
                    fs.setCount(toInteger(statsMap.get("count")));
                    fs.setScore(toInteger(statsMap.get("score")));
                    fs.setMentions_count(toInteger(statsMap.get("mentions_count")));
                    fl.setStats(fs);
                }
                tmpFlavorList.add(fl);
            }
            this.flavorList = tmpFlavorList;
        }
    }

    // ======================
    // Nested: Style
    // ======================
    @SuppressWarnings("unchecked")
    @JsonProperty("style")
    private void unpackNestedStyle(Map<String, Object> style) {
        if (style == null) return;

        this.body = toInteger(style.get("body"));
        this.body_description = (String) style.get("body_description");

        List<Map<String, Object>> foodArray = (List<Map<String, Object>>) style.get("food");
        if (foodArray != null) {
            List<Food> tmpFoodList = new ArrayList<>();
            for (Map<String, Object> f : foodArray) {
                Food food = new Food();
                food.setId(toInteger(f.get("id")));
                food.setName((String) f.get("name"));
                food.setWeight(toDouble(f.get("weight")));
                tmpFoodList.add(food);
            }
            this.foodList = tmpFoodList;
        }
    }

    // ======================
    // METODI DI SUPPORTO
    // ======================
    private Double toDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    //CLASSI DI SUPPORTO
    
    //HO DEFINITO LE SEGUENTI CLASSI PER TRATTARE I DATI ANNIDATI DI VIVINO 
    //SI POSSONO SPOSTARE IN UN ALTRO FILE PER CHIAREZZA
    
    //CLASSE FLAVOR
    public static class Flavor {
        private String group;
        private FlavorStats stats;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public FlavorStats getStats() {
            return stats;
        }

        public void setStats(FlavorStats stats) {
            this.stats = stats;
        }
    }

    // ======================
    // CLASSE NIDIFICATA: FlavorStats
    // ======================
    public static class FlavorStats {
        private Integer count;
        private Integer score;
        private Integer mentions_count;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Integer getMentions_count() {
            return mentions_count;
        }

        public void setMentions_count(Integer mentions_count) {
            this.mentions_count = mentions_count;
        }
    }

    // ======================
    // CLASSE NIDIFICATA: Food
    // ======================
    public static class Food {
        private Integer id;
        private String name;
        private Double weight;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }
    }

    //Ridefinisco toString() per stampare
    @Override
    public String toString() {
        return "Wine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", variety='" + variety + '\'' +
                ", region='" + region + '\'' +
                ", description='" + description + '\'' +
                ", country='" + country + '\'' +
                ", alcohol_percentage='" + alcohol_percentage + '\'' +
                ", provenance='" + provenance + '\'' +
                ", points='" + points + '\'' +
                ", taster_name='" + taster_name + '\'' +
                ", province='" + province + '\'' +
                ", winery_id='" + winery_id + '\'' +
                ", winery_name='" + winery_name + '\'' +
                ", acidity=" + acidity +
                ", fizziness=" + fizziness +
                ", intensity=" + intensity +
                ", sweetness=" + sweetness +
                ", tannin=" + tannin +
                ", user_structure_count=" + user_structure_count +
                ", flavorList=" + flavorList +
                ", body=" + body +
                ", body_description='" + body_description + '\'' +
                ", foodList=" + foodList +
                '}';
    }
}
