package com.gms.alquimiapay.modules.transaction.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CashTransferRequestPayload
{
    @NotNull(message = "feeReference cannot be empty")
    @NotEmpty(message = "feeReference cannot be empty")
    @NotBlank(message = "feeReference cannot be blank")
    private String feeReference;

    @NotNull(message = "amount cannot be empty")
    @NotEmpty(message = "amount cannot be empty")
    @NotBlank(message = "amount cannot be blank")
    private String amount;

    @NotNull(message = "beneficiaryPhone cannot be empty")
    @NotEmpty(message = "beneficiaryPhone cannot be empty")
    @NotBlank(message = "beneficiaryPhone cannot be blank")
    private String beneficiaryPhone;

    @NotNull(message = "beneficiaryFullName cannot be empty")
    @NotEmpty(message = "beneficiaryFullName cannot be empty")
    @NotBlank(message = "beneficiaryFullName cannot be blank")
    private String beneficiaryFullName;

    private String currency;

    @NotNull(message = "pin cannot be empty")
    @NotEmpty(message = "pin cannot be empty")
    @NotBlank(message = "pin cannot be blank")
    private String pin;
}
