package com.gms.alquimiapay.modules.account.payload.data;

import lombok.Data;

@Data
public class AccountBillingDetails {
    private String name;
    private String city;
    private String country;
    private String line1;
    private String line2;
    private String district;
    private String postalCode;
}
