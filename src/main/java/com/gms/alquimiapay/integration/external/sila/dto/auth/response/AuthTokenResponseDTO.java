package com.gms.alquimiapay.integration.external.sila.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gms.alquimiapay.integration.external.sila.dto.auth.data.AccessTokenResponseData;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AuthTokenResponseDTO
{
    private boolean success;

    @JsonProperty("access_token")
    @SerializedName("access_token")
    private AccessTokenResponseData accessToken;
}
