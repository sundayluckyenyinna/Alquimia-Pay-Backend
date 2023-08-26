package com.gms.alquimiapay.integration.external.sila.dto.auth.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AccessTokenResponseData
{
    private String token;
    private long expiration;

    @JsonProperty("expiration_dt")
    @SerializedName("expiration_dt")
    private String expirationAt;
}
