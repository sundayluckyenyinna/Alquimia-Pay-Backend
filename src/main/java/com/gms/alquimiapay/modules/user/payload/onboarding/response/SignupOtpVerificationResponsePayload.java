package com.gms.alquimiapay.modules.user.payload.onboarding.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gms.alquimiapay.modules.user.payload.onboarding.data.SignupVerificationResponseData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignupOtpVerificationResponsePayload extends BaseResponse
{
    private SignupVerificationResponseData responseData;
    private SignupOtpVerificationResponsePayload(){}
    public static SignupOtpVerificationResponsePayload createInstance(){
        return new SignupOtpVerificationResponsePayload();
    }
}
