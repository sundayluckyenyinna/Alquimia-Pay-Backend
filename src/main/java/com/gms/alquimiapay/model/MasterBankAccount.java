package com.gms.alquimiapay.model;

import com.gms.alquimiapay.modules.account.payload.data.AccountBillingDetails;
import com.gms.alquimiapay.modules.account.payload.data.BankAddress;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_master_bank_account")
@Getter
@Setter
public class MasterBankAccount
{
    private static final Gson JSON = new Gson();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internal_ref", columnDefinition = "text")
    private String internalRef;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "routing_number")
    private String routingNumber;

    @Column(name = "billing_details_json", columnDefinition = "text")
    private String billingDetailsJson;

    @Column(name = "bank_address_json", columnDefinition = "text")
    private String bankAddressJson;

    @Column(name = "status")
    private String status;

    public AccountBillingDetails getAccountBillingDetails(){
        return JSON.fromJson(this.getBillingDetailsJson(), AccountBillingDetails.class);
    }

    public BankAddress getBankAddress(){
        return JSON.fromJson(this.getBankAddressJson(), BankAddress.class);
    }
}
