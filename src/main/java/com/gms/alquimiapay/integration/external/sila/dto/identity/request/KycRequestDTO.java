package com.gms.alquimiapay.integration.external.sila.dto.identity.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class KycRequestDTO 
{
    private SilaIdentityHeader header;
    private String message;
    @SerializedName("kyc_level")
    private String kycLevel;
}
