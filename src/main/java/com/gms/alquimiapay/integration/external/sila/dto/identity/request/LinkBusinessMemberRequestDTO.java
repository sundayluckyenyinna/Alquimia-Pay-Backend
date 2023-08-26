package com.gms.alquimiapay.integration.external.sila.dto.identity.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaBusinessHeader;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class LinkBusinessMemberRequestDTO
{
    private SilaBusinessHeader header;

    private String role;

    @SerializedName("member_handle")
    private String memberHandle;

    private String details;

    @SerializedName("ownership_stake")
    private Double ownershipStake;
}
