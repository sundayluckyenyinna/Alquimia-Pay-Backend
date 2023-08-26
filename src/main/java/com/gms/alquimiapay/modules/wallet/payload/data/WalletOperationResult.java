package com.gms.alquimiapay.modules.wallet.payload.data;

import lombok.Data;

@Data
public class WalletOperationResult
{
    private Boolean hasError;
    private String responseCode;
    private String responseMessage;
}
