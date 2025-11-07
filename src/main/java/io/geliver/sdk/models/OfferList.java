package io.geliver.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferList {
    private BigDecimal percentageCompleted;
    private Offer cheapest;
    private List<Offer> list;

    public BigDecimal getPercentageCompleted() { return percentageCompleted; }
    public void setPercentageCompleted(BigDecimal percentageCompleted) { this.percentageCompleted = percentageCompleted; }
    public Offer getCheapest() { return cheapest; }
    public void setCheapest(Offer cheapest) { this.cheapest = cheapest; }
    public List<Offer> getList() { return list; }
    public void setList(List<Offer> list) { this.list = list; }

    public boolean isReady() {
        return (percentageCompleted != null && percentageCompleted.doubleValue() >= 99.0) || cheapest != null;
    }
}

