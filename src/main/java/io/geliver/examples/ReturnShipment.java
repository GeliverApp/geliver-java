package io.geliver.examples;

import io.geliver.sdk.GeliverClient;
import io.geliver.sdk.models.Shipment;
import io.geliver.sdk.models.Transaction;

public class ReturnShipment {
    public static void main(String[] args) throws Exception {
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
        Shipment returned = client.shipments().createReturn(shipmentId, new java.util.HashMap<>());
        System.out.println("Return shipment: " + returned.getId());
        System.out.println("Label is not purchased yet. This example waits for offers and buys it with acceptOffer.");

        Shipment current = returned;
        long deadline = System.currentTimeMillis() + 60_000L;
        while (current.getOffers() == null || current.getOffers().getCheapest() == null || current.getOffers().getCheapest().getId() == null) {
            if (System.currentTimeMillis() >= deadline) {
                System.err.println("Timed out waiting for return offers.");
                return;
            }
            var progress = current.getOffers() != null ? current.getOffers().getPercentageCompleted() : java.math.BigDecimal.ZERO;
            System.out.println("Waiting offers... " + progress + "%");
            Thread.sleep(1000L);
            current = client.shipments().get(returned.getId());
        }

        Transaction tx = client.transactions().acceptOffer(current.getOffers().getCheapest().getId());
        System.out.println("Transaction: " + tx.getId());
        System.out.println("Purchased return shipment: " + (tx.getShipment() != null ? tx.getShipment().getId() : null));
    }
}
