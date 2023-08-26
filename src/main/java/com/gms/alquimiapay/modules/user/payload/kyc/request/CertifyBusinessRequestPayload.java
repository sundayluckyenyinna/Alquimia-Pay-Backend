package com.gms.alquimiapay.modules.user.payload.kyc.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CertifyBusinessRequestPayload
{
    @NotNull(message = "businessEmail cannot be null")
    @NotEmpty(message = "businessEmail cannot be empty")
    @NotBlank(message = "businessEmail cannot be blank")
    @Email(message = "businessEmail must be a valid email address")
    private String businessEmail;
}
