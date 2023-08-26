package com.gms.alquimiapay.modules.transaction.payload.data;

import lombok.Data;

@Data
public class BeneficiaryDetails
{
    private String name;
    private String account;
    private String phone;
}
