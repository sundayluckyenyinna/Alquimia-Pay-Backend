package com.gms.alquimiapay.integration.external.sila.dto.wallet.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaManyWalletResponseDTO extends GenericSilaResponseDTO
{
    private List<SilaWalletResponseData> wallets;
}
