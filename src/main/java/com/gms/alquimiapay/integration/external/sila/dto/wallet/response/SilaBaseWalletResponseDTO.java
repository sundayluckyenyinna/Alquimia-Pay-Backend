package com.gms.alquimiapay.integration.external.sila.dto.wallet.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaBaseWalletResponseDTO extends GenericSilaResponseDTO
{
    @SerializedName("wallet_id")
    private String walletId;

    @SerializedName("wallet_nickname")
    private String walletNickname;

    @SerializedName("queued_for_whitelist")
    private Boolean queuedForWhitelist;
}
