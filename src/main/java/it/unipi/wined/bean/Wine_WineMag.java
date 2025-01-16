package it.unipi.wined.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 *
 * @author nicol
 */

public class Wine_WineMag {
    
    @JsonProperty("_id")
    private String id;
    
    private String name;   
    private int price;     
    private String variety;
    private String region;
    private String description;
    private String country;
    private int alcohol_percentage;
    private String provenance; // "W" per WineMag

    private int points;
    private String taster_name;
    private String province;

    // ==============
    // Winery (annidato)
    // ==============
    @JsonProperty("id")
    private String winery_id;
    
    private String winery_name;

    // ==============
    // Costruttori
    // ==============
    public Wine_WineMag() {
        // costruttore vuoto per Jackson
    }

    // Esempio di costruttore
    public Wine_WineMag(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Se desideri un costruttore completo, puoi aggiungerlo:
    public Wine_WineMag(
            String id,
            String name,
            int price,
            String variety,
            String region,
            String description,
            String country,
            int alcohol_percentage,
            String provenance,
            int points,
            String taster_name,
            String province,
            String winery_id,
            String winery_name
    ) {
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
    }

    // ==============
    // Getter & Setter
    // ==============
    public String getId() {
        return id;
    }

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

    public int getAlcohol_percentage() {
        return alcohol_percentage;
    }

    public void setAlcohol_percentage(int alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
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

    // ==============
    // Unpack annidato "winery" (campo comune anche in WineMag)
    // ==============
    @SuppressWarnings("unchecked")
    @JsonProperty("winery")
    private void unpackNestedWinery(Map<String, Object> winery) {
        if (winery != null) {
            Object wId = winery.get("id");
            if (wId != null) {
                this.winery_id = String.valueOf(wId);
            }
            this.winery_name = (String) winery.get("name");
        }
    }

    @Override
    public String toString() {
        return "Wine_WineMag{" +
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
                '}';
    }
}
