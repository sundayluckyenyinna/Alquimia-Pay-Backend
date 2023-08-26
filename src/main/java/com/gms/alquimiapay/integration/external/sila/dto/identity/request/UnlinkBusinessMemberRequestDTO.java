package com.gms.alquimiapay.integration.external.sila.dto.identity.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaBusinessHeader;
import lombok.Data;

@Data
public class UnlinkBusinessMemberRequestDTO
{
    private SilaBusinessHeader header;
    private String role;
}
