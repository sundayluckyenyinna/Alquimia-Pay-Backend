package com.gms.alquimiapay.modules.transaction.payload.request;

import com.gms.alquimiapay.constants.CurrencyCode;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class TransactionFeeRequestPayload
{
    @NotNull(message = "transactionType cannot be null")
    @NotEmpty(message = "transactionType cannot be empty")
    @NotBlank(message = "transactionType cannot be blank")
    @Pattern(regexp = "^(CARD_PAYMENT|TRANSFER_PAYMENT)$", message = "transactionType must be one of CARD_PAYMENT, TRANSFER_PAYMENT")
    private String transactionType;

    @NotNull(message = "amount cannot be null")
    @NotEmpty(message = "amount cannot be empty")
    @NotBlank(message = "amount cannot be blank")
    private String amount;

    @NotNull(message = "receivingCurrency cannot be null")
    @NotEmpty(message = "receivingCurrency cannot be empty")
    @NotBlank(message = "receivingCurrency cannot be blank")
    @Pattern(regexp = "[A-Z]{3}", message = "receivingCurrency must be a 3 - upper-case lettered currency code.")
    private String receivingCurrency = CurrencyCode.MXN.name();
}
