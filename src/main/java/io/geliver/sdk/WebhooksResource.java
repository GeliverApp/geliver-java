package io.geliver.sdk;

import java.util.List;
import java.util.Map;

public class WebhooksResource {
    private final GeliverClient c;
    WebhooksResource(GeliverClient c) { this.c = c; }

    public Map<String,Object> create(String url, String type) {
        var body = type == null ? Map.of("url", url) : Map.of("url", url, "type", type);
        return c.request("POST", "/webhook", null, body, Map.class);
    }
    public List<Map<String,Object>> list() {
        return c.request("GET", "/webhook", null, null, List.class);
    }
    public Map<String,Object> delete(String webhookId) {
        return c.request("DELETE", "/webhook/"+encode(webhookId), null, null, Map.class);
    }
    public Map<String,Object> test(String type, String url) {
        var body = Map.of("type", type, "url", url);
        return c.request("PUT", "/webhook", null, body, Map.class);
    }
    private static String encode(String s) { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8); }
}

