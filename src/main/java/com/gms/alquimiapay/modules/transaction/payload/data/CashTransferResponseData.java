package com.gms.alquimiapay.modules.transaction.payload.data;

import lombok.Data;

@Data
public class CashTransferResponseData {
    private String internalReference;
    private String externalReference;
    private String feeReference;
    private String transactionType;
    private String amount;
    private String totalFee;
    private String createdAt;
    private String internalStatus;
    private String externalStatus;
    private String description;
    private String receivingAmount;
    private BeneficiaryDetails beneficiaryDetails;
}
