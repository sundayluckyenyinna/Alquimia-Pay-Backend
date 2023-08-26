package com.gms.alquimiapay.modules.wallet.payload.data;

import lombok.Data;

@Data
public class WalletData
{
    private String walletId;
    private String walletName;
    private String ownerFullName;
    private Boolean isDefault;
    private String blockchainAddress;
    private String blockchainNetwork;
    private Boolean isWhiteListed;
    private String availableBalance;
    private String pendingBalance;
    private String status;
}
