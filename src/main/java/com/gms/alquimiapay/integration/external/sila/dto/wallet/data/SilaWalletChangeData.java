package com.gms.alquimiapay.integration.external.sila.dto.wallet.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaWalletChangeData
{
    private String attribute;

    @SerializedName("old_value")
    private Object oldValue;

    @SerializedName("new_value")
    private Object newValue;
}
