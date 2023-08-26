package com.gms.alquimiapay.modules.transaction.payload.response;

import com.gms.alquimiapay.modules.transaction.payload.data.TransactionHistoryResponseData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransactionHistoryResponsePayload extends BaseResponse
{
    private List<TransactionHistoryResponseData> responseData;
}
