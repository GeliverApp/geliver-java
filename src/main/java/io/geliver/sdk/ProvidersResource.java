package io.geliver.sdk;

import java.util.List;
import java.util.Map;

public class ProvidersResource {
    private final GeliverClient c;
    ProvidersResource(GeliverClient c) { this.c = c; }

    public List<Map<String,Object>> listAccounts() {
        return c.request("GET", "/provideraccounts", null, null, List.class);
    }
    public Map<String,Object> createAccount(Map<String,Object> body) {
        return c.request("POST", "/provideraccounts", null, body, Map.class);
    }
    public Map<String,Object> deleteAccount(String providerAccountId, Boolean isDeleteAccountConnection) {
        Map<String, Object> q =
                isDeleteAccountConnection == null
                        ? null
                        : Map.<String, Object>of("isDeleteAccountConnection", isDeleteAccountConnection);
        return c.request("DELETE", "/provideraccounts/"+encode(providerAccountId), q, null, Map.class);
    }
    private static String encode(String s) { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
}
