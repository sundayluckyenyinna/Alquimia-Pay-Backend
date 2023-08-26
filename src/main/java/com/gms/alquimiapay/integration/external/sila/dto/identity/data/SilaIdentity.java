package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaIdentity
{
    @SerializedName("identity_alias")
    private String identityAlias;

    @SerializedName("identity_value")
    private String identityValue;

    @SerializedName("added_epoch")
    private Long addedEpoch;

    @SerializedName("modified_epoch")
    private Long modifiedEpoch;

    private String uuid;
}
