package io.geliver.sdk;

import java.util.List;
import java.util.Map;

public class ParcelTemplatesResource {
    private final GeliverClient c;
    ParcelTemplatesResource(GeliverClient c) { this.c = c; }

    public Map<String,Object> create(Map<String,Object> body) {
        return c.request("POST", "/parceltemplates", null, body, Map.class);
    }
    public List<Map<String,Object>> list() {
        return c.request("GET", "/parceltemplates", null, null, List.class);
    }
    public Map<String,Object> delete(String templateId) {
        return c.request("DELETE", "/parceltemplates/"+encode(templateId), null, null, Map.class);
    }
    private static String encode(String s) { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
}

