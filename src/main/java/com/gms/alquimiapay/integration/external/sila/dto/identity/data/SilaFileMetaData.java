package com.gms.alquimiapay.integration.external.sila.dto.identity.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SilaFileMetaData
{
    private String name;

    @SerializedName("file_name")
    private String fileName;

    private String hash;

    @SerializedName("mime_type")
    private String mimeType;

    @SerializedName("document_type")
    private String documentType;

    private String description;
}
