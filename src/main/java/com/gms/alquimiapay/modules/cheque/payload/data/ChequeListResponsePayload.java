package com.gms.alquimiapay.modules.cheque.payload.data;

import lombok.Data;

import java.util.List;

@Data
public class ChequeListResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private List<ChequeResponseData> responseData;
}
