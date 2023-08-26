package com.gms.alquimiapay.modules.cheque.payload.response;

import com.gms.alquimiapay.modules.cheque.payload.data.ChequeResponseData;
import lombok.Data;

@Data
public class SubmitChequeDepositResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private ChequeResponseData responseData;
}
