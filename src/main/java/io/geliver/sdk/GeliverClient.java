package io.geliver.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class GeliverClient {
    public static final String DEFAULT_BASE_URL = "https://api.geliver.io/api/v1";

    private final String baseUrl;
    private final String token;
    private final HttpClient http;
    private final int maxRetries;
    final ObjectMapper mapper;

    private final ShipmentsResource shipments;
    private final TransactionsResource transactions;
    private final AddressesResource addresses;
    private final WebhooksResource webhooks;
    private final ProvidersResource providers;
    private final ParcelTemplatesResource parcelTemplates;
    private final PricesResource prices;
    private final GeoResource geo;
    private final OrganizationsResource organizations;

    public GeliverClient(String token) {
        this(token, null, null, 2);
    }

    public GeliverClient(String token, String baseUrl, HttpClient http, int maxRetries) {
        this.baseUrl = (baseUrl == null ? DEFAULT_BASE_URL : baseUrl).replaceAll("/+$", "");
        this.token = token;
        this.http = http != null ? http : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.maxRetries = maxRetries;
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.shipments = new ShipmentsResource(this);
        this.transactions = new TransactionsResource(this);
        this.addresses = new AddressesResource(this);
        this.webhooks = new WebhooksResource(this);
        this.providers = new ProvidersResource(this);
        this.parcelTemplates = new ParcelTemplatesResource(this);
        this.prices = new PricesResource(this);
        this.geo = new GeoResource(this);
        this.organizations = new OrganizationsResource(this);
    }

    public ShipmentsResource shipments() { return shipments; }
    public TransactionsResource transactions() { return transactions; }
    public AddressesResource addresses() { return addresses; }
    public WebhooksResource webhooks() { return webhooks; }
    public ProvidersResource providers() { return providers; }
    public ParcelTemplatesResource parcelTemplates() { return parcelTemplates; }
    public PricesResource prices() { return prices; }
    public GeoResource geo() { return geo; }
    public OrganizationsResource organizations() { return organizations; }

    <T> T request(String method, String path, Map<String, Object> query, Object body, Class<T> outClass) {
        try {
            String url = baseUrl + path;
            if (query != null && !query.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (var e : query.entrySet()) {
                    if (e.getValue() == null) continue;
                    if (sb.length() > 0) sb.append('&');
                    sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                      .append('=')
                      .append(URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8));
                }
                if (sb.length() > 0) url += "?" + sb;
            }
            String bodyStr = null;
            if (body != null) {
                bodyStr = mapper.writeValueAsString(body);
            }
            HttpRequest.Builder rb = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json");
            switch (method) {
                case "GET" -> rb.GET();
                case "DELETE" -> rb.DELETE();
                default -> rb.method(method, HttpRequest.BodyPublishers.ofString(bodyStr == null ? "" : bodyStr));
            }

            int attempt = 0;
            while (true) {
                HttpResponse<String> res = http.send(rb.build(), HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() >= 400) {
                    if (shouldRetry(res.statusCode()) && attempt < maxRetries) {
                        attempt++;
                        backoff(attempt);
                        continue;
                    }
                    throw new RuntimeException("HTTP " + res.statusCode());
                }
                if (outClass == null) return null;
                String text = res.body() == null ? "{}" : res.body();
                // unwrap envelope if present
                JsonNode root = mapper.readTree(text);
                if (root.has("data")) {
                    JsonNode data = root.get("data");
                    return mapper.treeToValue(data, outClass);
                }
                return mapper.readValue(text, outClass);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    byte[] downloadBytes(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() >= 400) throw new RuntimeException("download error: " + res.statusCode());
            return res.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    String downloadString(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 400) throw new RuntimeException("download error: " + res.statusCode());
            return res.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean shouldRetry(int status) { return status == 429 || status >= 500; }
    static void backoff(int attempt) {
        try { Thread.sleep(Math.min(2000, 200 * (1 << (attempt - 1)))); } catch (InterruptedException ignored) {}
    }
}
