package com.gms.alquimiapay.modules.wallet.payload.response;

import com.gms.alquimiapay.modules.wallet.payload.data.WalletUpdateChange;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class WalletUpdateResponsePayload extends BaseResponse
{
    List<WalletUpdateChange> responseData;
}
