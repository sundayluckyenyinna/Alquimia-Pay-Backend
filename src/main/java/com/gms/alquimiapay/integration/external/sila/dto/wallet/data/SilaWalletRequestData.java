package com.gms.alquimiapay.integration.external.sila.dto.wallet.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaWalletRequestData
{
    @SerializedName("wallet_id")
    private String walletId;

    private String nickname;

    @SerializedName("default")
    private Boolean defaultWallet;

    @SerializedName("blockchain_address")
    private String blockchainAddress;

    @SerializedName("blockchain_network")
    private String blockchainNetwork;
}
