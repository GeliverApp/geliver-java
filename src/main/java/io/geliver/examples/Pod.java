package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import java.util.HashMap;

public class Pod {
  public static void main(String[] args) {
    String token = System.getenv("GELIVER_TOKEN");
    if (token == null || token.isBlank()) {
      System.err.println("GELIVER_TOKEN required");
      return;
    }
    var client = new GeliverClient(token);

    var sender = client.addresses().createSender(new HashMap<>() {
      {
        put("name", "POD Sender");
        put("email", "sender@example.com");
        put("phone", "+905000000098");
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
            put("name", "POD Recipient");
            put("phone", "+905000000001");
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
        put("providerServiceCode", "PTT_KAPIDA_ODEME");
        put("productPaymentOnDelivery", true);
        put("order", new HashMap<>() {
          {
            put("orderNumber", "POD-12345");
            put("totalAmount", "150");
            put("totalAmountCurrency", "TL");
          }
        });
      }
    });

    System.out.println("transaction id: " + (tx.getId() == null ? "" : tx.getId()));
  }
}
