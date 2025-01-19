package it.unipi.wined.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wine_WineVivino extends AbstractWine {

    private String winery_id;
    private String winery_name;
    private Double acidity;
    private Double fizziness;
    private Double intensity;
    private Double sweetness;
    private Double tannin;
    private List<Flavor> flavorList;
    private Integer body;
    private String body_description;
    private List<Food> foodList;

    public Wine_WineVivino() {
        super();
        this.flavorList = new ArrayList<>();
        this.foodList = new ArrayList<>();
    }

    public Wine_WineVivino(String id, String name, int price, int alcohol_percentage,
                           String description, String country, String region, String provenance,
                           String variety, String winery_id, String winery_name, Double acidity,
                           Double fizziness, Double intensity, Double sweetness, Double tannin,
                           List<Flavor> flavorList, Integer body, String body_description,
                           List<Food> foodList) {
        super(id, name, price, description, country, region, provenance, variety, alcohol_percentage);
        this.winery_id = winery_id;
        this.winery_name = winery_name;
        this.acidity = acidity;
        this.fizziness = fizziness;
        this.intensity = intensity;
        this.sweetness = sweetness;
        this.tannin = tannin;
        this.flavorList = flavorList;
        this.body = body;
        this.body_description = body_description;
        this.foodList = foodList;
    }

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
        Map<String, Object> structure = (Map<String, Object>) taste.get("structure");
        if (structure != null) {
            this.acidity   = toDouble(structure.get("acidity"));
            this.fizziness = toDouble(structure.get("fizziness"));
            this.intensity = toDouble(structure.get("intensity"));
            this.sweetness = toDouble(structure.get("sweetness"));
            this.tannin    = toDouble(structure.get("tannin"));
        }
        List<Map<String, Object>> flavorArray = (List<Map<String, Object>>) taste.get("flavor");
        if (flavorArray == null) return;
        List<Flavor> tempFlavorList = new ArrayList<>();
        for (Map<String, Object> flavorObj : flavorArray) {
            Flavor fl = new Flavor();
            fl.setGroup((String) flavorObj.get("group"));
            fl.setMentions_count(toInteger(flavorObj.get("mentions_count")));
            tempFlavorList.add(fl);
        }
        this.flavorList = tempFlavorList;
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

    @Override
    public String toString() {
        return "Wine_WineVivino{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", alcohol_percentage=" + alcohol_percentage +
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flavor {
        private String group;
        private Integer mentions_count;
        public Flavor() {}
        public Flavor(String group, Integer mentions_count) {
            this.group = group;
            this.mentions_count = mentions_count;
        }
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
        @Override
        public String toString() {
            return "Flavor{" +
                   "group='" + group + '\'' +
                   ", mentions_count=" + mentions_count +
                   '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Food {
        private String name;
        public Food() {}
        public Food(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return "Food{" + "name='" + name + '\'' + '}';
        }
    }
}
