package it.unipi.wined.bean;

/**
 *
 * @author nicol
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wine_WineMag extends AbstractWine {

    private String winery_id;
    private String winery_name;

    public Wine_WineMag() {
        super();
    }

    public Wine_WineMag(String id, String name, int price, String variety, 
                        String region, String description, String country, 
                        int alcohol_percentage, String provenance, 
                        String winery_id, String winery_name) {
        super(id, name, price, description, country, region, provenance, variety, alcohol_percentage);
        this.winery_id = winery_id;
        this.winery_name = winery_name;
    }

   

    public String getWinery_id() {
        return winery_id;
    }

    public String getWinery_name() {
        return winery_name;
    }

    public void setWinery_id(String winery_id) {
        this.winery_id = winery_id;
    }

    public void setWinery_name(String winery_name) {
        this.winery_name = winery_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public void setAlcohol_percentage(int alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
    }
    
    

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
                ", alcohol_percentage=" + alcohol_percentage +
                ", provenance='" + provenance + '\'' +
                ", winery_id='" + winery_id + '\'' +
                ", winery_name='" + winery_name + '\'' +
                '}';
    }
}
