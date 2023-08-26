package com.gms.alquimiapay.modules.account.model;

import com.gms.alquimiapay.modules.report.annotation.ExcelHeader;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_account_deposit")
@Getter
@Setter
public class AccountDeposit
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelHeader(name = "S/N")
    private Long id;

    @Column(name = "internal_ref", columnDefinition = "text")
    @ExcelHeader(name = "INTERNAL_REFERENCE")
    private String internalRef;

    @Column(name = "deposit_id", columnDefinition = "text")
    @ExcelHeader(name = "DEPOSIT_ID")
    private String depositId;

    @Column(name = "source_wallet_id", columnDefinition = "text")
    @ExcelHeader(name = "SOURCE_WALLET_ID")
    private String sourceWalletId;

    @Column(name = "source_card_id", columnDefinition = "text")
    @ExcelHeader(name = "SOURCE_CARD_ID")
    private String sourceCardId;

    @Column(name = "source_type", columnDefinition = "text")
    @ExcelHeader(name = "SOURCE_TYPE")
    private String sourceType;

    @Column(name = "source_blockchain_address", columnDefinition = "text")
    @ExcelHeader(name = "SOURCE_BLOCKCHAIN_ADDRESS")
    private String sourceBlockchainAddress;

    @Column(name = "source_blockchain")
    @ExcelHeader(name = "SOURCE_BLOCKCHAIN")
    private String sourceBlockchain;

    @Column(name = "created_at")
    @ExcelHeader(name = "CREATED_AT")
    private String createdAt;

    @Column(name = "updated_at")
    @ExcelHeader(name = "UPDATED_AT")
    private String updatedAt;

    @Column(name = "beneficiary_type")
    @ExcelHeader(name = "BENEFICIARY_TYPE")
    private String beneficiaryType;

    @Column(name = "beneficiary_wallet_account_id", columnDefinition = "text")
    @ExcelHeader(name = "BENEFICIARY_WALLET_OR_ACCOUNT_ID")
    private String beneficiaryWalletOrAccountId;

    @Column(name = "beneficiary_account_name")
    @ExcelHeader(name = "BENEFICIARY_ACCOUNT_NAME")
    private String beneficiaryAccountName;

    @Column(name = "amount")
    @ExcelHeader(name = "AMOUNT")
    private String amount;

    @Column(name = "currency")
    @ExcelHeader(name = "CURRENCY")
    private String currency;

    @Column(name = "source_customer_name")
    @ExcelHeader(name = "SOURCE_CUSTOMER_NAME")
    private String sourceCustomerName;

    @Column(name = "source_customer_email")
    @ExcelHeader(name = "SOURCE_CUSTOMER_EMAIL")
    private String sourceCustomerEmail;

    @Column(name = "status")
    @ExcelHeader(name = "STATUS")
    private String status;

    @Column(name = "owner_wallet_balance")
    @ExcelHeader(name = "CUSTOMER_WALLET_BALANCE")
    private String ownerWalletBalance;

    @Column(name = "transaction_type")
    @ExcelHeader(name = "TRANSACTION_TYPE")
    private String transactionType;

    @Override
    public String toString() {
        return "AccountDeposit{" +
                "id=" + id +
                ", internalRef='" + internalRef + '\'' +
                ", depositId='" + depositId + '\'' +
                ", sourceWalletId='" + sourceWalletId + '\'' +
                ", sourceCardId='" + sourceCardId + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", sourceBlockchainAddress='" + sourceBlockchainAddress + '\'' +
                ", sourceBlockchain='" + sourceBlockchain + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", beneficiaryType='" + beneficiaryType + '\'' +
                ", beneficiaryWalletOrAccountId='" + beneficiaryWalletOrAccountId + '\'' +
                ", beneficiaryAccountName='" + beneficiaryAccountName + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", sourceCustomerName='" + sourceCustomerName + '\'' +
                ", sourceCustomerEmail='" + sourceCustomerEmail + '\'' +
                ", status='" + status + '\'' +
                ", ownerWalletBalance='" + ownerWalletBalance + '\'' +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}
