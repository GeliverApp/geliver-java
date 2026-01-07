package io.geliver.sdk;

import io.geliver.sdk.models.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TransactionsResource {
    private final GeliverClient c;
    TransactionsResource(GeliverClient c) { this.c = c; }

    public Transaction acceptOffer(String offerId) {
        return c.request("POST", "/transactions", null, Map.of("offerID", offerId), Transaction.class);
    }

    /** One-step label purchase: post shipment details directly to /transactions. */
    public Transaction create(Map<String, Object> body) {
        Map<String, Object> shipmentBody = body;
        Object wrapperProviderServiceCode = null;
        Object wrapperProviderAccountID = null;
        if (body != null && body.get("shipment") instanceof Map<?,?> m) {
            @SuppressWarnings("unchecked") Map<String, Object> sm = (Map<String, Object>) m;
            shipmentBody = sm;
            wrapperProviderServiceCode = body.get("providerServiceCode");
            wrapperProviderAccountID = body.get("providerAccountID");
        }

        Map<String, Object> shipment = normalizeShipment(shipmentBody);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("shipment", shipment);
        if (wrapperProviderServiceCode != null) shipment.remove("providerServiceCode");
        Object providerServiceCode = wrapperProviderServiceCode != null ? wrapperProviderServiceCode : shipment.remove("providerServiceCode");
        if (providerServiceCode != null) wrapper.put("providerServiceCode", providerServiceCode);
        if (wrapperProviderAccountID != null) shipment.remove("providerAccountID");
        Object providerAccountID = wrapperProviderAccountID != null ? wrapperProviderAccountID : shipment.remove("providerAccountID");
        if (providerAccountID != null) wrapper.put("providerAccountID", providerAccountID);
        return c.request("POST", "/transactions", null, wrapper, Transaction.class);
    }

    /** One-step label purchase accepting any shipment-like object (maps or POJOs). */
    public Transaction create(Object body) {
        return create(toMap(body));
    }

    /** Explicit helper that takes a shipment-like payload and posts it to /transactions. */
    public Transaction createFromShipment(Object shipment) {
        return create(shipment);
    }

    private Map<String, Object> normalizeShipment(Map<String, Object> body) {
        Map<String, Object> copy = new HashMap<>(body == null ? Map.of() : body);
        Object rv = copy.get("recipientAddress");
        if (rv instanceof Map<?,?> rmap) {
            @SuppressWarnings("unchecked") Map<String,Object> m = new HashMap<>((Map<String,Object>)rmap);
            Object ph = m.get("phone");
            if (ph == null || String.valueOf(ph).isBlank()) throw new IllegalArgumentException("recipientAddress.phone is required");
            copy.put("recipientAddress", m);
        }
        Object ov = copy.get("order");
        if (ov instanceof Map<?,?> omap) {
            @SuppressWarnings("unchecked") Map<String,Object> m = new HashMap<>((Map<String,Object>)omap);
            if (!m.containsKey("sourceCode") || m.get("sourceCode") == null || String.valueOf(m.get("sourceCode")).isBlank()) {
                m.put("sourceCode", "API");
            }
            copy.put("order", m);
        }
        for (var k : new String[]{"length","width","height","weight"}) {
            if (copy.containsKey(k) && copy.get(k) != null) copy.put(k, String.valueOf(copy.get(k)));
        }
        return copy;
    }

    private Map<String, Object> toMap(Object body) {
        if (body == null) return Map.of();
        if (body instanceof Map<?,?> m) {
            @SuppressWarnings("unchecked") Map<String,Object> out = new HashMap<>((Map<String,Object>) m);
            return out;
        }
        return c.mapper.convertValue(body, new com.fasterxml.jackson.core.type.TypeReference<Map<String,Object>>() {});
    }
}
