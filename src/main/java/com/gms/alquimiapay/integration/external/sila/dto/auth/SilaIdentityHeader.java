package com.gms.alquimiapay.integration.external.sila.dto.auth;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaIdentityHeader
{
    private long created;

    @SerializedName("app_handle")
    private String appHandle;

    @SerializedName("user_handle")
    private String userHandle;

    private String version;
    private String crypto;
    private String reference;

}
