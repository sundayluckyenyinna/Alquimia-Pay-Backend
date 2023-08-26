package com.gms.alquimiapay.modules.transaction.payload.response;

import com.gms.alquimiapay.modules.transaction.payload.data.TransactionFeeResponseData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionFeeResponsePayload extends BaseResponse
{
    private TransactionFeeResponseData responseData;
}
