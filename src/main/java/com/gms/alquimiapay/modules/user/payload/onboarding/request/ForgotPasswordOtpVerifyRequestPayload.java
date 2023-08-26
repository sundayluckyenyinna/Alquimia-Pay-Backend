package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ForgotPasswordOtpVerifyRequestPayload {

    @NotNull(message = "email cannot be null")
    @NotEmpty(message = "email cannot be empty")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotNull(message = "otp cannot be null")
    @NotEmpty(message = "otp cannot be empty")
    @NotBlank(message = "otp cannot be blank")
    @Pattern(regexp = "[0-9]{6}", message = "otp must be a 6 digit number string")
    private String otp;

    @NotNull(message = "newPassword cannot be null")
    @NotEmpty(message = "newPassword cannot be empty")
    @NotBlank(message = "newPassword cannot be blank")
    private String newPassword;

    @NotNull(message = "deviceId cannot be null")
    @NotEmpty(message = "deviceId cannot be empty")
    @NotBlank(message = "deviceId cannot be blank")
    private String deviceId;

}
