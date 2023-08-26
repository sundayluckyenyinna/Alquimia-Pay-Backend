package com.gms.alquimiapay.integration.external.sila.dto.identity.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import lombok.Data;

@Data
public class SilaBaseRequestDTO
{
    private SilaIdentityHeader header;
    private String message;
}
