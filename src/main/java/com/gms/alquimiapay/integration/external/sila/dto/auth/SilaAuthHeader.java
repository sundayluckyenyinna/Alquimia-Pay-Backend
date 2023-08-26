package com.gms.alquimiapay.integration.external.sila.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaAuthHeader
{
    private long created;

    @JsonProperty("auth_handle")
    @SerializedName("auth_handle")
    private String authHandle;

    private String version;
    private String reference;
}
