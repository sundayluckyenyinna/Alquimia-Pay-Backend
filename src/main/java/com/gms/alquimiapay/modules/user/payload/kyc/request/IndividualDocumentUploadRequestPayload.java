package com.gms.alquimiapay.modules.user.payload.kyc.request;

import lombok.Data;

@Data
public class IndividualDocumentUploadRequestPayload
{
    private String userEmail;

    private String name;

    private String mimeType;

    private String fileName;

    private String fileExtension;

    private String fileSize;

    private String fileDescription;

    private String documentType;

    private String fileType;

    private String externalReference;

    private String fileContent;

    private String identityType;

}
