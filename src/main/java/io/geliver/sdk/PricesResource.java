package io.geliver.sdk;

import java.util.HashMap;
import java.util.Map;

public class PricesResource {
    private final GeliverClient c;
    PricesResource(GeliverClient c) { this.c = c; }

    public Map<String,Object> listPrices(String paramType, String length, String width, String height, String weight, String distanceUnit, String massUnit) {
        var q = new HashMap<String,Object>();
        q.put("paramType", paramType);
        q.put("length", length);
        q.put("width", width);
        q.put("height", height);
        q.put("weight", weight);
        if (distanceUnit != null) q.put("distanceUnit", distanceUnit);
        if (massUnit != null) q.put("massUnit", massUnit);
        return c.request("GET", "/priceList", q, null, Map.class);
    }
}

