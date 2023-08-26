package com.gms.alquimiapay.modules.report.dto;

import lombok.Data;


@Data
public class AccountDepositDTO
{

    private String internalRef;

    private String depositId;

    private String sourceWalletId;

    private String sourceCardId;

    private String sourceType;

    private String sourceBlockchainAddress;

    private String sourceBlockchain;

    private String createdAt;

    private String updatedAt;

    private String beneficiaryType;

    private String beneficiaryWalletOrAccountId;

    private String beneficiaryAccountName;

    private String amount;

    private String currency;

    private String sourceCustomerName;

    private String sourceCustomerEmail;

    private String status;

    private String ownerWalletBalance;

    private String transactionType;

}
