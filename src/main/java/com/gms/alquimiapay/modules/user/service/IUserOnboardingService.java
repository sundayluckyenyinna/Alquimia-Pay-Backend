package com.gms.alquimiapay.modules.user.service;

import com.gms.alquimiapay.modules.user.payload.onboarding.request.ForgotPasswordOtpVerifyRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.SignupOtpVerificationRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UserLoginRequestPayload;
import com.gms.alquimiapay.modules.user.payload.onboarding.request.UserSignupRequestPayload;

public interface IUserOnboardingService
{
    String processSignupUser(UserSignupRequestPayload requestPayload);
    String processSignupOtpVerification(SignupOtpVerificationRequestPayload requestPayload);
    String processUserLogin(UserLoginRequestPayload requestPayload);
    String processUserLogout(String authToken);
    String processForgotPasswordOtpRequest(String authToken);
    String processForgotPasswordOtpVerification(String authToken, ForgotPasswordOtpVerifyRequestPayload requestPayload);
    String processResetPassword(String authToken, String newPassword, String deviceId);
    String processNewDeviceLinkOtpRequest(String authToken, String newDeviceId);
    String processNewDeviceLinkOtpVerification(String authToken, String newDeviceId, String otp);

}
