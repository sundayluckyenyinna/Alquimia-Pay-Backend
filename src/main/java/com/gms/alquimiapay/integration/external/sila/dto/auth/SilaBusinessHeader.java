package com.gms.alquimiapay.integration.external.sila.dto.auth;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaBusinessHeader extends SilaIdentityHeader
{
    @SerializedName("business_handle")
    private String businessHandle;
}
