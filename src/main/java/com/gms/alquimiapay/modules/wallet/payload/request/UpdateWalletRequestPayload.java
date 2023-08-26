package com.gms.alquimiapay.modules.wallet.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateWalletRequestPayload
{
    @NotNull(message = "walletId cannot be null")
    @NotEmpty(message = "walletId cannot be empty")
    @NotBlank(message = "walletId cannot be blank")
    private String walletId;

    @NotNull(message = "walletName cannot be null")
    @NotEmpty(message = "walletName cannot be empty")
    @NotBlank(message = "walletName cannot be blank")
    private String walletName;

    private Boolean isDefaultWallet;

    private String userHandle;
}
