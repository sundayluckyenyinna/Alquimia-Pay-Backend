package com.gms.alquimiapay.modules.storage.service;

import com.gms.alquimiapay.modules.storage.model.FileUpload;
import com.gms.alquimiapay.modules.storage.payload.FileUploadListRequestPayload;
import com.gms.alquimiapay.modules.storage.payload.FileUploadListResponsePayload;
import com.gms.alquimiapay.payload.BaseResponse;


public interface IFtpClientService {

    BaseResponse processFileStorage(String base64Content, FileUpload fileUpload, boolean saveToRemote);

    FileUploadListResponsePayload processFileUploadListForAdmin(FileUploadListRequestPayload requestPayload);

    FileUploadListResponsePayload processFileUploadListForUser(String email, String startDate, String endDate, String folderPrefix);

    // GENERAL UTILITY
    FileUpload processSingleFileUploadById(String fileUUID);
}
