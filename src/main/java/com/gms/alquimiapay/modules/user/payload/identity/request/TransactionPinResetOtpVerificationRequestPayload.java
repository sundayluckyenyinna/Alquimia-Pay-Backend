package com.gms.alquimiapay.modules.user.payload.identity.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class TransactionPinResetOtpVerificationRequestPayload
{
    @NotNull(message = "otp cannot be null")
    @NotEmpty(message = "otp cannot be empty")
    @NotBlank(message = "otp cannot be blank")
    private String otp;

    @NotNull(message = "newPin cannot be null")
    @NotEmpty(message = "newPin cannot be empty")
    @NotBlank(message = "newPin cannot be blank")
    private String newPin;

    @NotNull(message = "channel cannot be null")
    @NotEmpty(message = "channel cannot be empty")
    @NotBlank(message = "channel cannot be blank")
    @Pattern(regexp = "^(MOBILE|WEB|CONSOLE)$", message = "channel must be of MOBILE, WEB or CONSOLE")
    private String channel;

    private String deviceId;
}
