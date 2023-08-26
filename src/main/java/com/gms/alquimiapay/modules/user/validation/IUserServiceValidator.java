package com.gms.alquimiapay.modules.user.validation;

import com.gms.alquimiapay.modules.user.payload.onboarding.data.UserCredentials;
import com.gms.alquimiapay.payload.ValidationPayload;

public interface IUserServiceValidator
{
    ValidationPayload processUserExistsValidation(UserCredentials userCredentials);
    ValidationPayload processChannelValidation(String channel, String deviceId);
    ValidationPayload processUserExistsByEmailValidation(String emailAddress);

    ValidationPayload processUserTypeValidation(String userTye, String businessName);

    void validateUserExistValidationThrowException(String email);
}
