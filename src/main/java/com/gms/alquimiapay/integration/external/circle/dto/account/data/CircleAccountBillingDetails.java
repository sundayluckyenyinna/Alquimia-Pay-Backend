package com.gms.alquimiapay.integration.external.circle.dto.account.data;

import lombok.Data;

@Data
public class CircleAccountBillingDetails {
    private String name;
    private String city;
    private String country;
    private String line1;
    private String line2;
    private String district;
    private String postalCode;
}
