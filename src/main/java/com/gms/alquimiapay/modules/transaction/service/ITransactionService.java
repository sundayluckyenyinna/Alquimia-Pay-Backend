package com.gms.alquimiapay.modules.transaction.service;

import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import com.gms.alquimiapay.modules.transaction.payload.request.CashTransferRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.CreateBeneficiaryRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.TransactionFeeRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.request.TransactionHistoryRequestPayload;
import com.gms.alquimiapay.modules.transaction.payload.response.*;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.payload.BaseResponse;

public interface ITransactionService {

    TransactionFeeResponsePayload processTransactionFeeRequest(TransactionFeeRequestPayload requestPayload);
    CashTransferResponsePayload processCashTransferRequest(CashTransferRequestPayload requestPayload, String token);

    void submitRemittanceToAlquimiaForTransactionEntry(GmsUser user, TransactionEntry entry);

    TransactionHistoryResponsePayload processTransactionHistoryRequest(TransactionHistoryRequestPayload requestPayload, String authToken);

    TransactionStatusResponsePayload processTransactionStatus(String transactionId);

    BaseResponse processCashTransferFundReversal(String internalRef);

    CreateBeneficiaryResponsePayload processCustomerBeneficiaryCreation(CreateBeneficiaryRequestPayload requestPayload, String token);

    CustomerBeneficiaryListResponsePayload processGetCustomerBeneficiaryList(String authToken);
    Double getExchangeRate(String fromCurrency, String toCurrency);

}
