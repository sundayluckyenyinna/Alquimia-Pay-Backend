package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class DeviceLinkOtpVerifyRequestPayload
{
    @NotNull(message = "email cannot be null")
    @NotEmpty(message = "email cannot be empty")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotNull(message = "newDeviceId cannot be null")
    @NotEmpty(message = "newDeviceId cannot be empty")
    @NotBlank(message = "newDeviceId cannot be blank")
    private String newDeviceId;

    @NotNull(message = "otp cannot be null")
    @NotEmpty(message = "otp cannot be empty")
    @NotBlank(message = "otp cannot be blank")
    @Pattern(regexp = "[0-9]{6}", message = "otp must be a 6 digit number string")
    private String otp;
}
