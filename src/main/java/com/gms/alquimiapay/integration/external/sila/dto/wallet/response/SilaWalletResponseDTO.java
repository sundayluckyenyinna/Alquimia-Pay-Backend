package com.gms.alquimiapay.integration.external.sila.dto.wallet.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletRequestData;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaWalletResponseDTO extends GenericSilaResponseDTO
{
    private SilaWalletRequestData wallet;

    @SerializedName("is_whitelisted")
    private Boolean isWhiteListed;

    @SerializedName("sila_available_balance")
    private Double silaAvailableBalance;

    @SerializedName("sila_pending_balance")
    private Double silaPendingBalance;
}
