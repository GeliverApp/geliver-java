package io.geliver.sdk;

import io.geliver.sdk.models.Shipment;
import io.geliver.sdk.models.Transaction;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReturnShipmentTest {
    private static class CapturingClient extends GeliverClient {
        int calls;
        String method;
        String path;
        Map<?, ?> body;

        CapturingClient() { super("test"); }

        @Override
        <T> T request(String method, String path, Map<String, ?> query, Object body, Class<T> outClass) {
            calls++;
            this.method = method;
            this.path = path;
            this.body = (Map<?, ?>) body;
            return mapper.convertValue(Map.of("id", "return-1"), outClass);
        }
    }

    @Test
    void returnFlowsAllowOriginalAddressesAndPreserveCustomerOverride() {
        var customer = Map.of("name", "Customer", "phone", "+905051234567", "address1", "Pickup address");
        for (boolean purchase : new boolean[]{false, true}) {
            for (boolean override : new boolean[]{false, true}) {
                var client = new CapturingClient();
                var body = new HashMap<String, Object>();
                body.put("isReturn", false);
                body.put("count", 0);
                if (override) body.put("senderAddress", customer);

                if (purchase) {
                    body.put("willAccept", false);
                    Transaction returned = client.transactions().createReturn("original-1", body);
                    assertEquals("return-1", returned.getId());
                } else {
                    Shipment returned = client.shipments().createReturn("original-1", body);
                    assertEquals("return-1", returned.getId());
                }

                assertEquals(1, client.calls);
                assertEquals("POST", client.method);
                assertEquals("/shipments/original-1", client.path);
                assertEquals(true, client.body.get("isReturn"));
                assertEquals(1, client.body.get("count"));
                assertEquals(override ? customer : null, client.body.get("senderAddress"));
                assertFalse(client.body.containsKey("recipientAddress"));
                assertFalse(client.body.containsKey("recipientAddressID"));
                assertEquals(purchase ? Boolean.TRUE : null, client.body.get("willAccept"));
            }
        }
    }

    @Test
    void shipmentReturnRejectsImmediatePurchaseBeforeRequest() {
        var client = new CapturingClient();
        assertThrows(IllegalArgumentException.class,
            () -> client.shipments().createReturn("original-1", Map.of("willAccept", true)));
        assertEquals(0, client.calls);
    }
}
