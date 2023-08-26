package com.gms.alquimiapay.modules.user.payload.kyc.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CertifyBeneficialOwnerRequestPayload
{
    @NotNull(message = "ownerEmail cannot be null")
    @NotEmpty(message = "ownerEmail cannot be empty")
    @NotBlank(message = "ownerEmail cannot be blank")
    @Email(message = "ownerEmail must be a valid email address")
    private String ownerEmail;

    @NotNull(message = "businessEmail cannot be null")
    @NotEmpty(message = "businessEmail cannot be empty")
    @NotBlank(message = "businessEmail cannot be blank")
    @Email(message = "businessEmail must be a valid email address")
    private String businessEmail;

    @NotNull(message = "certificationToken cannot be null")
    @NotEmpty(message = "certificationToken cannot be empty")
    @NotBlank(message = "certificationToken cannot be blank")
    private String certificationToken;
}
