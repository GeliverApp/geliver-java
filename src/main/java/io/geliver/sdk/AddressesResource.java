package io.geliver.sdk;

import java.util.HashMap;
import java.util.Map;

public class AddressesResource {
    private final GeliverClient c;
    AddressesResource(GeliverClient c) { this.c = c; }

    public Map<String, Object> create(Map<String, Object> body) {
        return c.request("POST", "/addresses", null, body, Map.class);
    }

    public Map<String, Object> createSender(Map<String, Object> body) {
        Map<String, Object> copy = new HashMap<>(body);
        Object zip = copy.get("zip");
        if (zip == null || String.valueOf(zip).isBlank()) {
            throw new IllegalArgumentException("zip is required for sender addresses");
        }
        copy.put("isRecipientAddress", false);
        return create(copy);
    }

    public Map<String, Object> createRecipient(Map<String, Object> body) {
        Map<String, Object> copy = new HashMap<>(body);
        Object ph = copy.get("phone");
        if (ph == null || String.valueOf(ph).isBlank()) {
            throw new IllegalArgumentException("phone is required for recipient addresses");
        }
        copy.put("isRecipientAddress", true);
        return create(copy);
    }
}
