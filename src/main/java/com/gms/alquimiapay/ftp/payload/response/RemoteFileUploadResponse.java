package com.gms.alquimiapay.ftp.payload.response;

import lombok.Data;

@Data
public class RemoteFileUploadResponse {

    private String responseCode;
    private String publicLink;
    private String remoteFileName;

}
