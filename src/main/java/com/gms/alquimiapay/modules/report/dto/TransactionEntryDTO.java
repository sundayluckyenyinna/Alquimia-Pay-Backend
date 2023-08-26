package com.gms.alquimiapay.modules.report.dto;

import lombok.Data;


@Data
public class TransactionEntryDTO
{

    private String internalRef;

    private String externalRef;

    private String createdAt;

    private String createdBy;

    private String updatedAt;

    private String updatedBy;

    private Double exchangeRate;

    private String transactionType;

    private String transactionAmount;

    private String destinationAmount;

    private String customerName;

    private String customerEmail;

    private String customerBeneficiaryName;

    private String customerBeneficiaryPhone;

    private String customerBeneficiaryAccount;

    private String transactionFee;

    private String transactionTotalAmount;

    private String internalStatus;

    private String externalStatus;

    private Boolean isUpdatedByExternalVendor = false;

    private String vendor;

    private String feeReference;

    private String amountForFeeRequest;

    private Boolean isFeeUsed = false;

    private String transactionHash;

    private String sourceType;

    private String sourceWalletId;

    private String sourceCardId;

    private String sourceBlockchainAddress;

    private String sourceBlockchain;

    private String destinationType;

    private String destinationBlockChainAddress;

    private String destinationChain;

    private String failureReason;

    private String ownerWalletBalance;

    private String currency;

    private String destinationCurrency;

    private String description;
}
