package com.gms.alquimiapay.modules.wallet.payload.data;

import lombok.Data;

@Data
public class WalletUpdateChange
{
    private String attribute;
    private Object oldValue;
    private Object newValue;
}
