package io.geliver.sdk;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ShipmentAddressSelectionTest {
    @Test
    void allNewShipmentFlowsRequireOneAddressPerSide() {
        var client = new GeliverClient("test");
        List<Map<String, Object>> invalid = List.of(
            Map.of("recipientAddressID", "merchant-1"),
            Map.of("senderAddressID", "customer-1", "senderAddress", Map.of("address1", "A"), "recipientAddressID", "merchant-1"),
            Map.of("senderAddressID", "customer-1"),
            Map.of("senderAddressID", "customer-1", "recipientAddressID", "merchant-1", "recipientAddress", Map.of("address1", "B"))
        );

        for (var body : invalid) {
            assertThrows(IllegalArgumentException.class, () -> client.shipments().create(body));
            assertThrows(IllegalArgumentException.class, () -> client.shipments().createTest(body));
            assertThrows(IllegalArgumentException.class, () -> client.transactions().create(Map.of("shipment", body)));
        }
    }
}
