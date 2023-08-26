package com.gms.alquimiapay.modules.wallet.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateWalletRequestPayload
{
    @NotNull(message = "walletName cannot be null")
    @NotEmpty(message = "walletName cannot be empty")
    @NotBlank(message = "walletName cannot be blank")
    private String walletName;

    private Boolean isDefault = false;

    private String userHandle;
}
