package com.gms.alquimiapay.modules.cheque.service;

import com.gms.alquimiapay.modules.cheque.payload.SingleChequeResponsePayload;
import com.gms.alquimiapay.modules.cheque.payload.data.ChequeListResponsePayload;
import com.gms.alquimiapay.modules.cheque.payload.request.ChequeDepositRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.request.ChequeListRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.request.SubmitDepositChequeRequestPayload;
import com.gms.alquimiapay.modules.cheque.payload.response.SubmitChequeDepositResponsePayload;
import com.gms.alquimiapay.payload.BaseResponse;

public interface IChequeDepositService
{
    SubmitChequeDepositResponsePayload processChequeSubmission(String token, SubmitDepositChequeRequestPayload requestPayload);

    ChequeListResponsePayload processChequeListForUser(String token, String startDate, String endDate, String status);

    ChequeListResponsePayload processChequeListForAdmin(ChequeListRequestPayload requestPayload);

    BaseResponse processChequeDepositVerification(ChequeDepositRequestPayload requestPayload);

    BaseResponse processChequeDepositRejection(ChequeDepositRequestPayload requestPayload);

    SingleChequeResponsePayload processSingleChequeById(String chequeId);
}
