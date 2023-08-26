package com.gms.alquimiapay.modules.user.payload.onboarding.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gms.alquimiapay.modules.user.payload.onboarding.data.UserSignupResponseData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignupResponsePayload extends BaseResponse {
    private UserSignupResponseData responseData;

    private UserSignupResponsePayload(){}
    public static UserSignupResponsePayload createInstance(){
        return new UserSignupResponsePayload();
    }
}
