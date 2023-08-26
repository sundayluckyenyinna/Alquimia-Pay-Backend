package com.gms.alquimiapay.modules.transaction.payload.data;

import lombok.Data;

@Data
public class TransactionFeeResponseData {
    private String reference;
    private String transactionType;
    private String amount;
    private String integrationFee;
    private String internalFee;
    private String totalFee;
    private String receivingCurrency;
    private String receivingAmount;
    private Double exchangeRate;
}
