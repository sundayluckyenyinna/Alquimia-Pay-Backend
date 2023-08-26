package com.gms.alquimiapay.modules.wallet.payload.request;

import lombok.Data;

@Data
public class WalletRequestPayload
{
    private String walletName;
    private String walletId;
    private String userHandle;
}
