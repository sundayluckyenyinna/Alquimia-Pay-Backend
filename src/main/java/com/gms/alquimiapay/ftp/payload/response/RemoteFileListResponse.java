package com.gms.alquimiapay.ftp.payload.response;

import com.gms.alquimiapay.ftp.payload.data.RemoteFileData;
import lombok.Data;

import java.util.List;

@Data
public class RemoteFileListResponse
{
    private String responseCode;
    List<RemoteFileData> responseData;
}
