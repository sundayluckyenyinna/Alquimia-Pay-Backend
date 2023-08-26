package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaBusinessTypeResponseData
{
    private String uuid;
    private String name;
    private String label;

    @SerializedName("requires_certification")
    private boolean requiresCertification;
}
