package com.gms.alquimiapay.modules.cheque.payload;

import com.gms.alquimiapay.modules.cheque.payload.data.ChequeResponseData;
import lombok.Data;

@Data
public class SingleChequeResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private ChequeResponseData responseData;
}
