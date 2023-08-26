package com.gms.alquimiapay.modules.user.payload.onboarding.data;

import lombok.Data;

@Data
public class UserCredentials
{
    private String emailAddress;
    private String username;
    private String mobileNumber;
    private UserCredentials(){}
    public static UserCredentials getInstance(){
        return new UserCredentials();
    }
}
