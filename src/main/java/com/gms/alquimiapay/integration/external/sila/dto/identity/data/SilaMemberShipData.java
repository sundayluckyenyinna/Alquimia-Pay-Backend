package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaMemberShipData
{
    @SerializedName("business_handle")
    private String businessHandle;

    private String entityName;

    private String role;

    private String details;

    @SerializedName("ownership_stake")
    private String ownerShipStake;

    @SerializedName("certification_token")
    private String certificationToken;
}
