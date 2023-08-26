package com.gms.alquimiapay.webhook.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class BaseEventDTO
{
    @SerializedName("event_time")
    private String eventTime;

    @SerializedName("event_type")
    private String eventType;

    @SerializedName("event_uuid")
    private String eventUUID;
}
