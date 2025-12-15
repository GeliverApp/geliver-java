package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import io.geliver.sdk.RecipientAddressRequest;
import java.util.HashMap;

public class OwnAgreement {
  public static void main(String[] args) {
    String token = System.getenv("GELIVER_TOKEN");
    if (token == null || token.isBlank()) {
      System.err.println("GELIVER_TOKEN required");
      return;
    }
    var client = new GeliverClient(token);

    var sender = client.addresses().createSender(new HashMap<>() {
      {
        put("name", "OwnAg Sender");
        put("email", "sender@example.com");
        put("phone", "+905000000097");
        put("address1", "Hasan Mahallesi");
        put("countryCode", "TR");
        put("cityName", "Istanbul");
        put("cityCode", "34");
        put("districtName", "Esenyurt");
        put("zip", "34020");
      }
    });

    var recipient = new RecipientAddressRequest();
    recipient.name = "OwnAg Recipient";
    recipient.phone = "+905000000002";
    recipient.address1 = "Atatürk Mahallesi";
    recipient.countryCode = "TR";
    recipient.cityName = "Istanbul";
    recipient.cityCode = "34";
    recipient.districtName = "Esenyurt";

    var tx = client.transactions().createFromShipment(new HashMap<String, Object>() {
      {
        put("senderAddressID", sender.get("id"));
        put("recipientAddress", recipient);
        put("length", "10.0");
        put("width", "10.0");
        put("height", "10.0");
        put("distanceUnit", "cm");
        put("weight", "1.0");
        put("massUnit", "kg");
        put("providerServiceCode", "SURAT_STANDART");
        put("providerAccountID", "c0dfdb42-012d-438c-9d49-98d13b4d4a2b");
      }
    });

    System.out.println("transaction id: " + (tx.getId() == null ? "" : tx.getId()));
  }
}
