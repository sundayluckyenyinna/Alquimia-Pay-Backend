package com.gms.alquimiapay.modules.storage.payload;

import lombok.Data;

@Data
public class FileUploadListRequestPayload
{
    private String startDate;
    private String endDate;
    private String folderPrefix;
}
