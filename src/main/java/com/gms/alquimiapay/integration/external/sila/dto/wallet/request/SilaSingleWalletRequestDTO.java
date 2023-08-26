package com.gms.alquimiapay.integration.external.sila.dto.wallet.request;

import com.gms.alquimiapay.integration.external.sila.dto.identity.request.SilaBaseRequestDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaSingleWalletRequestDTO extends SilaBaseRequestDTO
{
    private String nickname;

    @SerializedName("wallet_id")
    private String walletId;

    @SerializedName("default")
    private Boolean defaultWallet;
}
