package com.gms.alquimiapay.modules.user.payload.onboarding.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BusinessOfficerRequestPayload
{
    @NotNull(message = "businessEmail cannot be null")
    @NotEmpty(message = "businessEmail cannot be empty")
    @NotBlank(message = "businessEmail cannot be blank")
    private String businessEmail;

    @NotNull(message = "officerEmail cannot be null")
    @NotEmpty(message = "officerEmail cannot be empty")
    @NotBlank(message = "officerEmail cannot be blank")
    private String officerEmail;

    private String details;

    private double ownerStake;
}
