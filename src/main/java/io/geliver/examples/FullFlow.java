package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import io.geliver.sdk.models.Shipment;
import io.geliver.sdk.models.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FullFlow {
    public static void main(String[] args) throws Exception {
        var token = System.getenv().getOrDefault("GELIVER_TOKEN", "YOUR_TOKEN");
        var client = new GeliverClient(token);

        Map<String,Object> sender = client.addresses().createSender(new HashMap<>() {{
            put("name", "ACME Inc."); put("email", "ops@acme.test"); put("address1", "Street 1");
            put("countryCode", "TR"); put("cityName", "Istanbul"); put("cityCode", "34");
            put("districtName", "Esenyurt"); put("districtID", 107605); put("zip", "34020");
        }});

        Shipment s = client.shipments().createTest(new HashMap<>() {{
            put("senderAddressID", sender.get("id"));
            put("recipientAddress", new HashMap<>() {{
                put("name", "John Doe"); put("email", "john@example.com"); put("address1", "Dest St 2");
                put("countryCode", "TR"); put("cityName", "Istanbul"); put("cityCode", "34");
                put("districtName", "Esenyurt"); put("districtID", 107605); put("zip", "34020");
            }});
            put("order", new HashMap<>() {{
                put("orderNumber", "ABC12333322");
                put("sourceIdentifier", "https://magazaadresiniz.com");
                put("totalAmount", "150");
                put("totalAmountCurrency", "TL");
            }});
            put("length", "10.0"); put("width", "10.0"); put("height", "10.0");
            put("distanceUnit", "cm"); put("weight", "1.0"); put("massUnit", "kg");
        }});

        var offers = s.getOffers();
        if (offers == null || !offers.isReady()) {
            long start = System.currentTimeMillis();
            while (true) {
                var s2 = client.shipments().get(s.getId());
                offers = s2.getOffers();
                if (offers != null && offers.isReady()) break;
                if (System.currentTimeMillis() - start > 60000) throw new RuntimeException("Timed out waiting offers");
                Thread.sleep(1000);
            }
        }

        Transaction tx = client.transactions().acceptOffer(offers.getCheapest().getId());
        System.out.println("tx " + tx.getId() + " paid=" + tx.getIsPayed());
        if (tx.getShipment() != null) {
            System.out.println("Barcode: " + tx.getShipment().getBarcode());
            System.out.println("Tracking number: " + tx.getShipment().getTrackingNumber());
            System.out.println("Label URL: " + tx.getShipment().getLabelURL());
            System.out.println("Tracking URL: " + tx.getShipment().getTrackingUrl());
        }
        if (tx.getShipment() != null && tx.getShipment().getLabelURL() != null) {
            byte[] pdf = client.shipments().downloadLabelByUrl(tx.getShipment().getLabelURL());
            Files.write(Path.of("label-java.pdf"), pdf);
        }
        if (tx.getShipment() != null && tx.getShipment().getResponsiveLabelURL() != null) {
            String html = client.shipments().downloadResponsiveLabelByUrl(tx.getShipment().getResponsiveLabelURL());
            Files.writeString(Path.of("label-java.html"), html);
        }
    }
}
