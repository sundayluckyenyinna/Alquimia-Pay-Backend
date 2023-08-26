package com.gms.alquimiapay.modules.transaction.payload.data;

import lombok.Data;

@Data
public class TransactionStatusResponseData
{
    private String externalRef;
    private String status;
    private String sourceWalletType;
    private String sourceWalletId;
    private String destinationType;
    private String destinationAddress;
    private String destinationChain;
    private String amount;
    private String currency;
    private String transactionHash;
    private String createDate;
    private String failureReason;
}
