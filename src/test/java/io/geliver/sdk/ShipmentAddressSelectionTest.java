package io.geliver.sdk;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShipmentAddressSelectionTest {
    @Test
    void newShipmentFlowsRequireRecipientAndAllowDefaultSender() {
        var client = new GeliverClient("test");
        List<Map<String, Object>> invalid = List.of(
            Map.of("senderAddressID", "merchant-1", "senderAddress", Map.of("address1", "A"), "recipientAddressID", "customer-1"),
            Map.of("senderAddressID", "merchant-1"),
            Map.of("senderAddressID", "merchant-1", "recipientAddressID", "customer-1", "recipientAddress", Map.of("address1", "B"))
        );

        for (var body : invalid) {
            assertThrows(IllegalArgumentException.class, () -> client.shipments().create(body));
            assertThrows(IllegalArgumentException.class, () -> client.shipments().createTest(body));
            assertThrows(IllegalArgumentException.class, () -> client.transactions().create(Map.of("shipment", body)));
        }

        assertDoesNotThrow(() -> ShipmentsResource.validateAddresses(Map.of("recipientAddressID", "customer-1")));
    }
}
