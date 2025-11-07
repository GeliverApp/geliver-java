package io.geliver.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Offer {
    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}

