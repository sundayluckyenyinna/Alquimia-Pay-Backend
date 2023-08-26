package com.gms.alquimiapay.modules.account.model;

import com.gms.alquimiapay.modules.account.payload.data.AccountBillingDetails;
import com.gms.alquimiapay.modules.account.payload.data.BankAddress;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_account")
@Getter
@Setter
public class VirtualAccountCache
{
    private static final Gson JSON = new Gson();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internal_id", columnDefinition = "text")
    private String internalId;

    @Column(name = "request_idempotency_id", columnDefinition = "text")
    private String requestIdempotencyId;

    @Column(name = "external_id", columnDefinition = "text")
    private String externalId;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "routing_number")
    private String routingNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "status")
    private String status;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "tracking_ref")
    private String trackingRef;

    @Column(name = "finger_print", columnDefinition = "text")
    private String fingerPrint;

    @Column(name = "billing_details_json", columnDefinition = "text")
    private String billingDetailsJson;

    @Column(name = "bank_address_json", columnDefinition = "text")
    private String bankAddressJson;

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "beneficiary_address_1")
    private String beneficiaryAddress1;

    @Column(name = "beneficiary_address_2")
    private String beneficiaryAddress2;

    @Column(name = "virtual_account_enabled")
    private Boolean virtualAccountEnabled;

    @Column(name = "internal_customer_name")
    private String internalCustomerName;

    @Column(name = "internal_customer_email")
    private String internalCustomerEmail;

    @Column(name = "linking_logs", columnDefinition = "text")
    private String linkingLogs;

    @Column(name = "creation_logs", columnDefinition = "text")
    private String creationLogs;

    public AccountBillingDetails getAccountBillingDetails(){
        return JSON.fromJson(this.getBillingDetailsJson(), AccountBillingDetails.class);
    }

    public BankAddress getBankAddress(){
        return JSON.fromJson(this.getBankAddressJson(), BankAddress.class);
    }
}
