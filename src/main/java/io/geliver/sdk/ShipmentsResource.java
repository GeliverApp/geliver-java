package io.geliver.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geliver.sdk.models.Shipment;

import java.util.HashMap;
import java.util.Map;

public class ShipmentsResource {
    private final GeliverClient c;
    ShipmentsResource(GeliverClient c) { this.c = c; }

    public Shipment create(Map<String, Object> body) { return c.request("POST", "/shipments", null, body, Shipment.class); }

    public Shipment createTest(Map<String, Object> body) {
        Map<String,Object> copy = new HashMap<>(body);
        copy.put("test", true);
        // normalize dimension fields to String
        for (var k : new String[]{"length","width","height","weight"}) {
            if (copy.containsKey(k) && copy.get(k) != null) copy.put(k, String.valueOf(copy.get(k)));
        }
        return create(copy);
    }

    public Shipment get(String shipmentId) { return c.request("GET", "/shipments/" + encode(shipmentId), null, null, Shipment.class); }

    public Shipment updatePackage(String shipmentId, Map<String, Object> body) {
        Map<String,Object> copy = new HashMap<>(body);
        for (var k : new String[]{"length","width","height","weight"}) {
            if (copy.containsKey(k) && copy.get(k) != null) copy.put(k, String.valueOf(copy.get(k)));
        }
        return c.request("PATCH", "/shipments/" + encode(shipmentId), null, copy, Shipment.class);
    }

    public Shipment cancel(String shipmentId) { return c.request("DELETE", "/shipments/" + encode(shipmentId), null, null, Shipment.class); }
    public Shipment clone(String shipmentId) { return c.request("POST", "/shipments/" + encode(shipmentId), null, null, Shipment.class); }

    public byte[] downloadLabelByUrl(String url) { return c.downloadBytes(url); }
    public String downloadResponsiveLabelByUrl(String url) { return c.downloadString(url); }

    private static String encode(String s) { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
}

