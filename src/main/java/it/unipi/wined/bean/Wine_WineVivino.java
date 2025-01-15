package it.unipi.wined.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nicol
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wine_WineVivino {

    private String _id;
    private String name;
    private int price;
    private String alcohol_percentage;
    private String description;
    private String country;
    private String region;
    private String provenance;
    private String variety;

    // ==============
    // Winery
    // ==============
    private String winery_id;
    private String winery_name;

    // ==============
    // taste
    // ==============
    private Double acidity;
    private Double fizziness;
    private Double intensity;
    private Double sweetness;
    private Double tannin;

    private List<Flavor> flavorList;

    // ==============
    // style
    // ==============
    private Integer body;
    private String body_description;
    private List<Food> foodList;

    // ==============
    // Costruttori
    // ==============
    public Wine_WineVivino() {
        this.flavorList = new ArrayList<>();
        this.foodList = new ArrayList<>();
    }

    // ==============
    // Getter & Setter
    // ==============
    
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getAlcohol_percentage() {
        return alcohol_percentage;
    }

    public void setAlcohol_percentage(String alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    //Winery
    public String getWinery_id() {
        return winery_id;
    }

    public String getWinery_name() {
        return winery_name;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("winery")
    private void unpackWinery(Map<String, Object> winery) {
        if (winery != null) {
            this.winery_id   = (String) winery.get("id"); 
            this.winery_name = (String) winery.get("name");
        }
    }

    //taste
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

    public List<Flavor> getFlavorList() {
        return flavorList;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("taste")
    private void unpackTaste(Map<String, Object> taste) {
        if (taste == null) return;

        // structure
        Map<String, Object> structure = (Map<String, Object>) taste.get("structure");
        if (structure != null) {
            this.acidity   = toDouble(structure.get("acidity"));
            this.fizziness = toDouble(structure.get("fizziness"));
            this.intensity = toDouble(structure.get("intensity"));
            this.sweetness = toDouble(structure.get("sweetness"));
            this.tannin    = toDouble(structure.get("tannin"));
        }

        // flavor
        List<Map<String, Object>> flavorArray = (List<Map<String, Object>>) taste.get("flavor");
        if (flavorArray == null) return;

        List<Flavor> tempFlavorList = new ArrayList<>();
        for (Map<String, Object> flavorObj : flavorArray) {
            Flavor fl = new Flavor();
            fl.setGroup((String) flavorObj.get("group"));
            // Mentions_count (se presente)
            fl.setMentions_count(toInteger(flavorObj.get("mentions_count")));
            tempFlavorList.add(fl);
        }
        this.flavorList = tempFlavorList;
}


    //style
    public Integer getBody() {
        return body;
    }

    public String getBody_description() {
        return body_description;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("style")
    private void unpackStyle(Map<String, Object> style) {
        if (style == null) return;

        this.body = toInteger(style.get("body"));
        this.body_description = (String) style.get("body_description");

        List<Map<String, Object>> foodArray = (List<Map<String, Object>>) style.get("food");
        if (foodArray == null) return;

        List<Food> tempFoodList = new ArrayList<>();
        
        for (Map<String, Object> foodObj : foodArray) {
            Food f = new Food();
            f.setName((String) foodObj.get("name"));
            tempFoodList.add(f);
        }
        
        this.foodList = tempFoodList;
    }


    // ==============
    // Metodi di supporto
    // ==============
    private Double toDouble(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number)val).doubleValue();
        try {
            return Double.valueOf(val.toString());
        } catch(NumberFormatException e) {
            return null;
        }
    }
    private Integer toInteger(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number)val).intValue();
        try {
            return Integer.valueOf(val.toString());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    // =====================
    //    CLASSI NIDIFICATE 
    //      DI SUPPORTO
    // =====================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flavor {
        private String group;
        private Integer mentions_count;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public Integer getMentions_count() {
            return mentions_count;
        }

        public void setMentions_count(Integer mentions_count) {
            this.mentions_count = mentions_count;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Food {
        private String name; 

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return "Wine_WineVivino{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", alcohol_percentage='" + alcohol_percentage + '\'' +
                ", description='" + description + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", provenance='" + provenance + '\'' +
                ", variety='" + variety + '\'' +
                ", winery_id='" + winery_id + '\'' +
                ", winery_name='" + winery_name + '\'' +
                ", acidity=" + acidity +
                ", fizziness=" + fizziness +
                ", intensity=" + intensity +
                ", sweetness=" + sweetness +
                ", tannin=" + tannin +
                ", flavorList=" + flavorList +
                ", body=" + body +
                ", body_description='" + body_description + '\'' +
                ", foodList=" + foodList +
                '}';
    }
}
