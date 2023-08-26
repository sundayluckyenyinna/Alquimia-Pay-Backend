package com.gms.alquimiapay.modules.user.payload.kyc.data;

import lombok.Data;

@Data
public class UserDocument
{
    private String name;

    private String mimeType;

    private String fileName;

    private String fileExtension;

    private String fileSize;

    private String fileDescription;

    private String documentType;

    private String fileType;

    private String createdAt;

    private String updatedAt;

    private String externalReference;

    private String hash;

    private String fileContent;

    private String identityType;

}
