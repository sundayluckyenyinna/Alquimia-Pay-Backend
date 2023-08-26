package com.gms.alquimiapay.modules.cheque.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChequeDepositRequestPayload {

    @NotNull(message = "chequeId cannot be null")
    @NotEmpty(message = "chequeId cannot be empty")
    @NotBlank(message = "chequeId cannot be blank")
    private String chequeId;

    @NotNull(message = "adminUser cannot be null")
    @NotEmpty(message = "adminUser cannot be empty")
    @NotBlank(message = "adminUser cannot be blank")
    private String adminUser;

    @NotNull(message = "adminPasscode cannot be null")
    @NotEmpty(message = "adminPasscode cannot be empty")
    @NotBlank(message = "adminPasscode cannot be blank")
    private String adminPasscode;

    @NotNull(message = "estimatedAmount cannot be null")
    @NotEmpty(message = "estimatedAmount cannot be empty")
    @NotBlank(message = "estimatedAmount cannot be blank")
    private String estimatedAmount;
}
