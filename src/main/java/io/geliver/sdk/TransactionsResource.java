package io.geliver.sdk;

import io.geliver.sdk.models.Transaction;

import java.util.Map;

public class TransactionsResource {
    private final GeliverClient c;
    TransactionsResource(GeliverClient c) { this.c = c; }

    public Transaction acceptOffer(String offerId) {
        return c.request("POST", "/transactions", null, Map.of("offerID", offerId), Transaction.class);
    }
}

