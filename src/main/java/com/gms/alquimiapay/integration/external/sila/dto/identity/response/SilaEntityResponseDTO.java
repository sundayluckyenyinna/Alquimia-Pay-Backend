package com.gms.alquimiapay.integration.external.sila.dto.identity.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaAddress;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaIdentity;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaMemberShipData;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaEntityResponseDTO extends GenericSilaResponseDTO
{
    SilaUserEntity entity;
    List<SilaAddress> addresses;
    List<SilaIdentity> identities;
    List<SilaMemberShipData> members;
}
