package io.geliver.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private String id;
    private Boolean isPayed;
    private Shipment shipment;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Boolean getIsPayed() { return isPayed; }
    public void setIsPayed(Boolean isPayed) { this.isPayed = isPayed; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
}

