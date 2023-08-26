package com.gms.alquimiapay.integration.external.sila.dto.wallet.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaWalletSearchFilter
{
    @SerializedName("page")
    private Integer pageNumber;

    @SerializedName("per_page")
    private Integer pageSize;

    @SerializedName("sort_ascending")
    private Boolean ascending;

    private String blockchainNetwork;

    @SerializedName("block_chain_address")
    private String blockchainAddress;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("wallet_id")
    private String walletId;
}
