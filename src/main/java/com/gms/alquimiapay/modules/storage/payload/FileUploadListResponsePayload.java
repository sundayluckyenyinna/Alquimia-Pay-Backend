package com.gms.alquimiapay.modules.storage.payload;

import com.gms.alquimiapay.modules.storage.model.FileUpload;
import lombok.Data;

import java.util.List;

@Data
public class FileUploadListResponsePayload
{
    private String responseCode;
    private List<FileUpload> fileUploads;
}
