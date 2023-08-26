package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SignupOtpVerificationRequestPayload
{
    @NotNull(message = "otp cannot be null")
    @NotEmpty(message = "otp cannot be empty")
    @NotBlank(message = "otp cannot be blank")
    private String otp;

    @NotNull(message = "emailAddress cannot be null")
    @NotBlank(message = "emailAddress cannot be blank")
    @NotEmpty(message = "emailAddress cannot be empty")
    @Email(message = "emailAddress must be a valid email with a valid email extension")
    private String emailAddress;

    @NotNull(message = "channel cannot be null")
    @Pattern(regexp = "^(MOBILE|WEB|CONSOLE)$", message = "channel must be of MOBILE, WEB or CONSOLE")
    private String channel;
}
