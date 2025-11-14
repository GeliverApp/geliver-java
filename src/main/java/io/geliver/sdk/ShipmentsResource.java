package io.geliver.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geliver.sdk.models.Shipment;

import java.util.HashMap;
import java.util.Map;

public class ShipmentsResource {
    private final GeliverClient c;
    ShipmentsResource(GeliverClient c) { this.c = c; }

    public Shipment create(Map<String, Object> body) {
        Map<String,Object> copy = new HashMap<>(body == null ? Map.of() : body);
        Object rv = copy.get("recipientAddress");
        if (rv instanceof Map<?,?> rmap) {
            Object ph = rmap.get("phone");
            if (ph == null || String.valueOf(ph).isBlank()) throw new IllegalArgumentException("recipientAddress.phone is required");
        }
        Object ov = copy.get("order");
        if (ov instanceof Map<?,?> omap) {
            @SuppressWarnings("unchecked") Map<String,Object> m = new HashMap<>((Map<String,Object>)omap);
            if (!m.containsKey("sourceCode") || m.get("sourceCode") == null || String.valueOf(m.get("sourceCode")).isEmpty()) {
                m.put("sourceCode", "API");
            }
            copy.put("order", m);
        }
        return c.request("POST", "/shipments", null, copy, Shipment.class);
    }

    public Shipment createTest(Map<String, Object> body) {
        Map<String,Object> copy = new HashMap<>(body);
        Object ov2 = copy.get("order");
        if (ov2 instanceof Map<?,?> omap2) {
            @SuppressWarnings("unchecked") Map<String,Object> m2 = new HashMap<>((Map<String,Object>)omap2);
            if (!m2.containsKey("sourceCode") || m2.get("sourceCode") == null || String.valueOf(m2.get("sourceCode")).isEmpty()) {
                m2.put("sourceCode", "API");
            }
            copy.put("order", m2);
        }
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
