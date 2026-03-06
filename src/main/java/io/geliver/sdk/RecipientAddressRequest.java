package io.geliver.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipientAddressRequest {
    @JsonProperty("name")
    public String name;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("email")
    public String email;

    @JsonProperty("address1")
    public String address1;

    @JsonProperty("address2")
    public String address2;

    @JsonProperty("countryCode")
    public String countryCode;

    @JsonProperty("cityName")
    public String cityName;

    @JsonProperty("cityCode")
    public String cityCode;

    @JsonProperty("districtName")
    public String districtName;

    @JsonProperty("districtID")
    public Integer districtID;

    @JsonProperty("zip")
    public String zip;
}
