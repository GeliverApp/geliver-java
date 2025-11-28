package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import java.util.HashMap;

public class OneStep {
  public static void main(String[] args) {
    String token = System.getenv("GELIVER_TOKEN");
    if (token == null || token.isBlank()) {
      System.err.println("GELIVER_TOKEN required");
      return;
    }
    var client = new GeliverClient(token);

    var sender = client.addresses().createSender(new HashMap<>() {
      {
        put("name", "OneStep Sender");
        put("email", "sender@example.com");
        put("phone", "+905000000099");
        put("address1", "Hasan Mahallesi");
        put("countryCode", "TR");
        put("cityName", "Istanbul");
        put("cityCode", "34");
        put("districtName", "Esenyurt");
        put("zip", "34020");
      }
    });

    var tx = client.transactions().create(new HashMap<>() {
      {
        put("senderAddressID", sender.get("id"));
        put("recipientAddress", new HashMap<>() {
          {
            put("name", "OneStep Recipient");
            put("phone", "+905000000000");
            put("address1", "Atatürk Mahallesi");
            put("countryCode", "TR");
            put("cityName", "Istanbul");
            put("cityCode", "34");
            put("districtName", "Esenyurt");
          }
        });
        put("length", "10.0");
        put("width", "10.0");
        put("height", "10.0");
        put("distanceUnit", "cm");
        put("weight", "1.0");
        put("massUnit", "kg");
      }
    });

    System.out.println("transaction id: " + (tx.getId() == null ? "" : tx.getId()));
  }
}
