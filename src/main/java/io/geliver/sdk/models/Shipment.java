package io.geliver.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipment {
    private String id;
    private String barcode;
    private String labelURL;
    private String responsiveLabelURL;
    private String trackingNumber;
    private String trackingUrl;
    private OfferList offers;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getLabelURL() { return labelURL; }
    public void setLabelURL(String labelURL) { this.labelURL = labelURL; }
    public String getResponsiveLabelURL() { return responsiveLabelURL; }
    public void setResponsiveLabelURL(String responsiveLabelURL) { this.responsiveLabelURL = responsiveLabelURL; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
    public OfferList getOffers() { return offers; }
    public void setOffers(OfferList offers) { this.offers = offers; }
}

