package com.gms.alquimiapay.modules.transaction.model;

import com.gms.alquimiapay.modules.report.annotation.ExcelHeader;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_transaction_entry")
@Setter
@Getter
public class TransactionEntry
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelHeader(name = "S/N")
    private Long id;

    @Column(name = "internal_ref", columnDefinition = "text")
    @ExcelHeader(name = "INTERNAL_REFERENCE")
    private String internalRef;

    @Column(name = "external_ref")
    @ExcelHeader(name = "EXTERNAL_REFERENCE")
    private String externalRef;

    @Column(name = "created_at")
    @ExcelHeader(name = "CREATED_AT")
    private String createdAt;

    @Column(name = "created_by")
    @ExcelHeader(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "updated_at")
    @ExcelHeader(name = "UPDATED_AT")
    private String updatedAt;

    @Column(name = "updated_by")
    @ExcelHeader(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "exchange_rate")
    @ExcelHeader(name = "EXCHANGE_RATE")
    private Double exchangeRate;

    @Column(name = "transaction_type")
    @ExcelHeader(name = "TRANSACTION_TYPE")
    private String transactionType;

    @Column(name = "transaction_amount")
    @ExcelHeader(name = "TRANSACTION_AMOUNT")
    private String transactionAmount;

    @Column(name = "destination_amount")
    @ExcelHeader(name = "DESTINATION_AMOUNT")
    private String destinationAmount;

    @Column(name = "customer_name")
    @ExcelHeader(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "customer_email")
    @ExcelHeader(name = "CUSTOMER_EMAIL")
    private String customerEmail;

    @Column(name = "customer_beneficiary_name")
    @ExcelHeader(name = "CUSTOMER_BENEFICIARY_NAME")
    private String customerBeneficiaryName;

    @Column(name = "customer_beneficiary_phone")
    @ExcelHeader(name = "CUSTOMER_BENEFICIARY_PHONE")
    private String customerBeneficiaryPhone;

    @Column(name = "customer_beneficiary_account")
    @ExcelHeader(name = "CUSTOMER_BENEFICIARY_ACCOUNT")
    private String customerBeneficiaryAccount;

    @Column(name = "transaction_fee")
    @ExcelHeader(name = "TRANSACTION_FEE")
    private String transactionFee;

    @Column(name = "transaction_total_amount")
    @ExcelHeader(name = "TRANSACTION_TOTAL_AMOUNT")
    private String transactionTotalAmount;

    @Column(name = "internal_status")
    @ExcelHeader(name = "INTERNAL_STATUS")
    private String internalStatus;

    @Column(name = "external_status")
    @ExcelHeader(name = "EXTERNAL_STATUS")
    private String externalStatus;

    @Column(name = "is_updated_by_external_vendor")
    @ExcelHeader(name = "IS_UPDATED_BY_EXTERNAL_VENDOR")
    private Boolean isUpdatedByExternalVendor = false;

    @Column(name = "vendor")
    @ExcelHeader(name = "VENDOR")
    private String vendor;

    @Column(name = "fee_reference", columnDefinition = "text")
    @ExcelHeader(name = "FEE_REFERENCE")
    private String feeReference;

    @Column(name = "amount_for_fee_request")
    @ExcelHeader(name = "AMOUNT_FOR_FEE_REQUEST")
    private String amountForFeeRequest;

    @Column(name = "is_fee_used")
    @ExcelHeader(name = "IS_FEE_USED")
    private Boolean isFeeUsed = false;

    @Column(name = "transaction_hash")
    @ExcelHeader(name = "TRANSACTION_HASH")
    private String transactionHash;

    @Column(name = "source_type")
    @ExcelHeader(name = "SOURCE_TYPE")
    private String sourceType;

    @Column(name = "source_wallet_id")
    @ExcelHeader(name = "SOURCE_WALLET_ID")
    private String sourceWalletId;

    @Column(name = "source_card_id", columnDefinition = "text")
    @ExcelHeader(name = "SOURCE_ID")
    private String sourceCardId;

    @Column(name = "source_blockchain_address", columnDefinition = "text")
    @ExcelHeader(name = "SOURCE_BLOCK_CHAIN_ADDRESS")
    private String sourceBlockchainAddress;

    @Column(name = "source_blockchain")
    @ExcelHeader(name = "SOURCE_BLOCK_CHAIN")
    private String sourceBlockchain;

    @Column(name = "destination_type")
    @ExcelHeader(name = "DESTINATION_TYPE")
    private String destinationType;

    @Column(name = "destination_blockchain_address")
    @ExcelHeader(name = "DESTINATION_BLOCK_CHAIN")
    private String destinationBlockChainAddress;

    @Column(name = "destination_chain")
    @ExcelHeader(name = "DESTINATION_CHAIN")
    private String destinationChain;

    @Column(name = "failure_reason")
    @ExcelHeader(name = "FAILURE_REASON")
    private String failureReason;

    @Column(name = "owner_wallet_balance")
    @ExcelHeader(name = "CUSTOMER_WALLET_BALANCE")
    private String ownerWalletBalance;

    @Column(name = "currency")
    @ExcelHeader(name = "SOURCE_CURRENCY")
    private String currency;

    @Column(name = "destination_currency")
    @ExcelHeader(name = "DESTINATION_CURRENCY")
    private String destinationCurrency;

    @Column(name = "description")
    @ExcelHeader(name = "DESCRIPTION")
    private String description;
}
