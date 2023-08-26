package com.gms.alquimiapay.modules.wallet.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateWalletResponsePayload extends BaseResponse
{
    WalletData responseData;
}
