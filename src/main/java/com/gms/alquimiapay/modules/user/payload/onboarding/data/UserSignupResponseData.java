package com.gms.alquimiapay.modules.user.payload.onboarding.data;

import lombok.Data;

@Data
public class UserSignupResponseData
{
    private String username;
    private String authToken;
    private String emailAddress;
    private String createdAt;
    private String updatedAt;
    private String status;
    private boolean requiresVerification;
    private boolean isVerified;
    private UserSignupResponseData(){}
    public static UserSignupResponseData createInstance(){
        return new UserSignupResponseData();
    }
}
