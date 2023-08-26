package com.gms.alquimiapay.modules.account.payload.data;

import lombok.Data;

@Data
public class WireVirtualAccountData
{
    private String accountNumber;
    private String routingNumber;
    private AccountBillingDetails billingDetails;
    private BankAddress bankAddress;
    private String customerName;
    private String status;
}
