package com.gms.alquimiapay.modules.user.payload.admin.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AdminUserRequestPayload
{
    @NotNull(message = "username cannot be null")
    @NotBlank(message = "username cannot be empty")
    @NotEmpty(message = "username cannot be empty")
    private String username;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be empty")
    @NotEmpty(message = "password cannot be empty")
    private String password;
}
