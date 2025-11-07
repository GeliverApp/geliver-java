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
        copy.put("isRecipientAddress", false);
        return create(copy);
    }

    public Map<String, Object> createRecipient(Map<String, Object> body) {
        Map<String, Object> copy = new HashMap<>(body);
        copy.put("isRecipientAddress", true);
        return create(copy);
    }
}

