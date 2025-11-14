package io.geliver.sdk;

import io.geliver.sdk.models.Transaction;

import java.util.Map;

public class TransactionsResource {
    private final GeliverClient c;
    TransactionsResource(GeliverClient c) { this.c = c; }

    public Transaction acceptOffer(String offerId) {
        return c.request("POST", "/transactions", null, Map.of("offerID", offerId), Transaction.class);
    }

    /** One-step label purchase: post shipment details directly to /transactions. */
    public Transaction create(Map<String, Object> body) {
        return c.request("POST", "/transactions", null, body, Transaction.class);
    }
}
