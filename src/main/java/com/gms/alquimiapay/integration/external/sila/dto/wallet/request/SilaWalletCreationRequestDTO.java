package com.gms.alquimiapay.integration.external.sila.dto.wallet.request;

import com.gms.alquimiapay.integration.external.sila.dto.identity.request.SilaBaseRequestDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaWalletCreationRequestDTO extends SilaBaseRequestDTO
{
    private SilaWalletRequestData wallet;
}
