package com.gms.alquimiapay.integration.external.sila.dto.identity.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessOwnerCertificationRequestDTO
{
    SilaIdentityHeader header;

    @SerializedName("member_handle")
    private String memberHandle;

    @SerializedName("certification_token")
    private String certificationToken;
}
