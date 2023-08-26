package com.gms.alquimiapay.modules.transaction.payload.pojo;

import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.model.MasterWallet;
import lombok.Data;

@Data
public class TransactionPojo
{
    private MasterWallet masterWallet;
    private MasterBankAccount masterBankAccount;
    private String destinationBlockchainAddress;
    private String destinationBlockchainAddressTag;
    private String amount;
    private String customerName;
    private String customerEmail;
    private String currency;
    private String chain;
}
