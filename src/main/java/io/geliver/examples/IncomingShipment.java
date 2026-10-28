package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import java.util.HashMap;
import java.util.Map;

public class IncomingShipment {
    public static void main(String[] args) {
        var token = System.getenv("GELIVER_TOKEN");
        if (token == null || token.isBlank()) {
            System.err.println("Set GELIVER_TOKEN.");
            return;
        }

        var client = new GeliverClient(token);
        var body = new HashMap<String, Object>();
        body.put("isReturn", true);
        body.put("test", true);
        body.put("recipientAddress", Map.of(
            "name", "Müşteri Adı", "email", "customer@example.com", "phone", "+905051234567",
            "address1", "Örnek Mahallesi 1", "countryCode", "TR",
            "cityCode", "34", "districtName", "Kadıköy", "zip", "34000"
        ));
        body.put("length", "10");
        body.put("width", "10");
        body.put("height", "10");
        body.put("distanceUnit", "cm");
        body.put("weight", "1");
        body.put("massUnit", "kg");

        var shipment = client.shipments().create(body);
        System.out.println("Gelecek gönderi: " + shipment.getId());
    }
}
