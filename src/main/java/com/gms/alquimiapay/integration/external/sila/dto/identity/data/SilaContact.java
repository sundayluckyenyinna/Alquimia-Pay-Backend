package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaContact
{
    private String phone;

    @SerializedName("contact_alias")
    private String contactAlias;

    private String email;
}
