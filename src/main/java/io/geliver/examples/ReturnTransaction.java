package io.geliver.examples;

import io.geliver.sdk.GeliverClient;

public class ReturnTransaction {
    public static void main(String[] args) {
        var token = System.getenv("GELIVER_TOKEN");
        var shipmentId = System.getenv("GELIVER_RETURN_SHIPMENT_ID");
        if ((shipmentId == null || shipmentId.isBlank()) && args.length > 0) {
            shipmentId = args[0];
        }
        if (token == null || token.isBlank() || shipmentId == null || shipmentId.isBlank()) {
            System.err.println("Set GELIVER_TOKEN and GELIVER_RETURN_SHIPMENT_ID, or pass the shipment ID as the first argument.");
            return;
        }

        var client = new GeliverClient(token);
        var tx = client.transactions().createReturn(shipmentId, new java.util.HashMap<>());
        System.out.println("Transaction: " + tx.getId());
        System.out.println("Purchased return shipment: " + (tx.getShipment() != null ? tx.getShipment().getId() : null));
    }
}
