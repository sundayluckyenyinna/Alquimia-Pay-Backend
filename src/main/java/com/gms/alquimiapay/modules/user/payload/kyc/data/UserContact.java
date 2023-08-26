package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;

@Data
public class UserContact
{
    private String phone;

    private String contactAlias;

    private String email;
}
