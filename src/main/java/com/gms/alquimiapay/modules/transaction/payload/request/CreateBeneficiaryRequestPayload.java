package com.gms.alquimiapay.modules.transaction.payload.request;

import lombok.Data;

@Data
public class CreateBeneficiaryRequestPayload
{
    private String name;
    private String phone;
    private String accountNumber;
    private String accountName;
    private String routingNumber;
    private String country;
    private String countryCode;
    private String email;
}
