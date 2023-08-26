package com.gms.alquimiapay.ftp.payload.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoteFileData
{
    private String remoteFileName;
    private String remoteParentDir;
    private String publicLink;
}
