package com.gms.alquimiapay.modules.cheque.payload.request;

import com.gms.alquimiapay.modules.cheque.payload.data.ChequeFileData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class SubmitDepositChequeRequestPayload
{

    @NotNull(message = "fileData cannot be null")
    private ChequeFileData fileData;

    private BigDecimal chequeAmount;

    private String accountNumber;

    private String accountName;

    private String chequeNumber;

    private String routingNumber;

    @NotNull(message = "currency cannot be null")
    @NotBlank(message = "currency cannot be blank")
    @NotEmpty(message = "currency cannot be empty")
    @Pattern(regexp = "[A-Z]{3}", message = "currency must be a 3 upper case letter code")
    private String currency;

    private String bank;
}
