package io.geliver.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private String id;
    private String offerID;
    private String shipmentID;
    private Boolean isPayed;
    private String transactionType;
    private Shipment shipment;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOfferID() { return offerID; }
    public void setOfferID(String offerID) { this.offerID = offerID; }
    public String getShipmentID() { return shipmentID; }
    public void setShipmentID(String shipmentID) { this.shipmentID = shipmentID; }
    public Boolean getIsPayed() { return isPayed; }
    public void setIsPayed(Boolean isPayed) { this.isPayed = isPayed; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
}
