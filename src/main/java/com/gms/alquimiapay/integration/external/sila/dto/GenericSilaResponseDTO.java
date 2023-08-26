package com.gms.alquimiapay.integration.external.sila.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericSilaResponseDTO
{
    private boolean success;
    private String message;
    private String status;
    private String reference;
    @SerializedName("response_time_ms")
    private String responseTimeMill;

    @SerializedName("user_handle")
    private String userHandle;

    @SerializedName("entity_type")
    private String entityType;
}
