package com.gms.alquimiapay.modules.account.payload.data;

import lombok.Data;

@Data
public class BankAddress {
    private String bankName;
    private String city;
    private String country;
    private String line1;
    private String line2;
    private String district;
}
