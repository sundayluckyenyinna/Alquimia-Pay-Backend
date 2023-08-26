package com.gms.alquimiapay.modules.user.payload.onboarding.data;

import lombok.Data;

@Data
public class SignupVerificationResponseData
{
    private String emailToVerify;
    private boolean isEmailVerified;
    private String verifiedAt;
    private String userStatus;
    private SignupVerificationResponseData(){}
    public static SignupVerificationResponseData createInstance(){
        return new SignupVerificationResponseData();
    }
}
