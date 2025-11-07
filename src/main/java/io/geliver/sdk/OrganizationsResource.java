package io.geliver.sdk;

import java.util.Map;

public class OrganizationsResource {
    private final GeliverClient c;
    OrganizationsResource(GeliverClient c) { this.c = c; }

    public Map<String,Object> getBalance(String organizationId) {
        return c.request("GET", "/organizations/" + encode(organizationId) + "/balance", null, null, Map.class);
    }
    private static String encode(String s) { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
}

