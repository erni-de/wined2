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

    private String province;
    private String winery_id;
    private String winery_name;

    public Wine_WineMag() {
        super();
    }

    public Wine_WineMag(String id, String name, int price, String variety, 
                        String region, String description, String country, 
                        int alcohol_percentage, String provenance, String province, 
                        String winery_id, String winery_name) {
        super(id, name, price, description, country, region, provenance, variety, alcohol_percentage);
        this.province = province;
        this.winery_id = winery_id;
        this.winery_name = winery_name;
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
                ", province='" + province + '\'' +
                ", winery_id='" + winery_id + '\'' +
                ", winery_name='" + winery_name + '\'' +
                '}';
    }
}
