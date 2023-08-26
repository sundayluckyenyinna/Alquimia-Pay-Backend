package com.gms.alquimiapay.integration.external.sila.dto.auth.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaAuthHeader;
import lombok.Data;

@Data
public class AuthTokenRequestDTO
{
    private SilaAuthHeader header;
}
