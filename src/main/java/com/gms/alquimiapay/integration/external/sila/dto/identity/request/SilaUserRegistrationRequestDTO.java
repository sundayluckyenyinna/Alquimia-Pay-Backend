package com.gms.alquimiapay.integration.external.sila.dto.identity.request;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaAddress;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaContact;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaIdentity;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaUserEntity;
import lombok.Data;

@Data
public class SilaUserRegistrationRequestDTO
{
    private SilaIdentityHeader header;
    private String message;
    private SilaAddress address;
    private SilaContact contact;
    private SilaIdentity identity;
    private SilaUserEntity entity;
}
