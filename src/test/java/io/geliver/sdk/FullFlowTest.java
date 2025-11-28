package io.geliver.sdk;

import io.geliver.sdk.models.Shipment;
import io.geliver.sdk.models.Transaction;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FullFlowTest {
    @Test
    void fullFlow_ifTokenPresent_runsEndToEnd() throws Exception {
        String token = System.getenv("GELIVER_TOKEN");
        Assumptions.assumeTrue(token != null && !token.isBlank(), "GELIVER_TOKEN not set; skipping");
        GeliverClient client = new GeliverClient(token);

        Map<String, Object> sender = client.addresses().createSender(new HashMap<>() {
            {
                put("name", "ACME Inc.");
                put("email", "ops@acme.test");
                put("address1", "Hasan Mahallesi");
                put("countryCode", "TR");
                put("cityName", "Istanbul");
                put("cityCode", "34");
                put("districtName", "Esenyurt");
                put("zip", "34020");
            }
        });
        assertNotNull(sender.get("id"));

        Shipment s = client.shipments().createTest(new HashMap<>() {
            {
                put("sourceCode", "API");
                put("senderAddressID", sender.get("id"));
                put("recipientAddress", new HashMap<>() {
                    {
                        put("name", "John Doe");
                        put("email", "john@example.com");
                        put("address1", "Dest St 2");
                        put("countryCode", "TR");
                        put("cityName", "Istanbul");
                        put("cityCode", "34");
                        put("districtName", "Esenyurt");
                        put("zip", "34020");
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
        assertNotNull(s.getId());

        var offers = s.getOffers();
        long start = System.currentTimeMillis();
        while (offers == null || !offers.isReady()) {
            s = client.shipments().get(s.getId());
            offers = s.getOffers();
            if (System.currentTimeMillis() - start > 60000)
                fail("Timed out waiting for offers");
            Thread.sleep(1000);
        }

        Transaction tx = client.transactions().acceptOffer(offers.getCheapest().getId());
        assertNotNull(tx.getId());
        assertTrue(Boolean.TRUE.equals(tx.getIsPayed()));
        if (tx.getShipment() != null && tx.getShipment().getLabelURL() != null) {
            byte[] pdf = client.shipments().downloadLabelByUrl(tx.getShipment().getLabelURL());
            assertTrue(pdf.length > 100);
            Files.write(Path.of("label-java-test.pdf"), pdf);
        }
    }
}
