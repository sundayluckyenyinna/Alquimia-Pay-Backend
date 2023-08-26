package com.gms.alquimiapay.modules.wallet.payload.response;

import com.gms.alquimiapay.modules.wallet.payload.data.WalletData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SingleWalletResponsePayload extends BaseResponse
{
    private WalletData responseData;
}
