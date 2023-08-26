package com.gms.alquimiapay.integration.internal.transaction;

import com.gms.alquimiapay.modules.transaction.payload.pojo.TransactionPojo;
import com.gms.alquimiapay.payload.BaseResponse;

public interface ITransactionIntegrationService
{
    BaseResponse processTransactionFeeRequest(String amount, String transactionType);
    BaseResponse processCashToBlockchainTransactionRequest(TransactionPojo pojo);

    BaseResponse processCashToBlockchainTransactionStatus(String id);
    BaseResponse processExchangeRate(String fromCurrency, String toCurrency);
}
