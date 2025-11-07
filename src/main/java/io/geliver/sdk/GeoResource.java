package io.geliver.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import io.geliver.sdk.models.City;
import io.geliver.sdk.models.District;

import java.util.List;
import java.util.Map;

public class GeoResource {
    private final GeliverClient c;
    GeoResource(GeliverClient c) { this.c = c; }

    public List<Map<String,Object>> listCities(String countryCode) {
        return c.request("GET", "/cities", Map.of("countryCode", countryCode), null, List.class);
    }
    public List<Map<String,Object>> listDistricts(String countryCode, String cityCode) {
        return c.request("GET", "/districts", Map.of("countryCode", countryCode, "cityCode", cityCode), null, List.class);
    }

    public List<City> listCitiesTyped(String countryCode) {
        return c.request("GET", "/cities", Map.of("countryCode", countryCode), null, new TypeReference<List<City>>() {});
    }
    public List<District> listDistrictsTyped(String countryCode, String cityCode) {
        return c.request("GET", "/districts", Map.of("countryCode", countryCode, "cityCode", cityCode), null, new TypeReference<List<District>>() {});
    }
}
