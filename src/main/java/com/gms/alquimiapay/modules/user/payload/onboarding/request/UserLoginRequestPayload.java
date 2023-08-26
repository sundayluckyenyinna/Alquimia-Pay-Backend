package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserLoginRequestPayload
{
    @NotEmpty(message = "emailAddress cannot be empty")
    @NotEmpty(message = "emailAddress cannot be empty")
    @NotNull(message = "emailAddress cannot be null")
    @Email(message = "emailAddress must be a valid email with a valid email extension.")
    private String emailAddress;

    @NotEmpty(message = "password cannot be empty")
    @NotBlank(message = "password cannot be blank")
    @NotNull(message = "password cannot be null")
    private String password;

    @NotEmpty(message = "channel cannot be empty")
    @NotBlank(message = "channel cannot be blank")
    @Pattern(regexp = "^(MOBILE|WEB|CONSOLE)$", message = "channel must be of MOBILE, WEB or CONSOLE")
    private String channel;

    private String deviceId;
}
