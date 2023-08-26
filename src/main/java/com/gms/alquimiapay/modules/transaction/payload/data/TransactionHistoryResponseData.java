package com.gms.alquimiapay.modules.transaction.payload.data;

import lombok.Data;

@Data
public class TransactionHistoryResponseData
{
    private String transactionType;
    private String createdAt;
    private String updatedAt;
    private String status;
    private String sourceWalletId;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryPhone;
    private String internalReference;
    private String externalReference;
    private String hash;
    private String amount;
    private String fee;
    private String totalAmount;
    private String currency;
    private String transactionEffect;
    private String walletBalance;
    private String description;
}
