package com.gms.alquimiapay.integration.external.sila.dto.wallet.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletChangeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaWalletUpdateResponseDTO extends GenericSilaResponseDTO
{
    private SilaWalletResponseData wallet;
    private List<SilaWalletChangeData> changes;
}
