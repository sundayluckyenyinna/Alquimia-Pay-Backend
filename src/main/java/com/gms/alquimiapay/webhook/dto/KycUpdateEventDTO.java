package com.gms.alquimiapay.webhook.dto;

import com.gms.alquimiapay.webhook.dto.data.KycEventUpdateData;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class KycUpdateEventDTO extends BaseEventDTO
{
    @SerializedName("event_details")
    private KycEventUpdateData eventDetails;
}
