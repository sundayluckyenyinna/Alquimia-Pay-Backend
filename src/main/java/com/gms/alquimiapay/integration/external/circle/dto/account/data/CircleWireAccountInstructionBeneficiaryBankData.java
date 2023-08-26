package com.gms.alquimiapay.integration.external.circle.dto.account.data;

import lombok.Data;

@Data
public class CircleWireAccountInstructionBeneficiaryBankData
{
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String swiftCode;
    private String routingNumber;
    private String accountNumber;
    private String currency;
}
