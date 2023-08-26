package com.gms.alquimiapay.modules.transaction.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gms.alquimiapay.modules.transaction.payload.data.CashTransferResponseData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class CashTransferResponsePayload extends BaseResponse {
    private CashTransferResponseData responseData;
}
