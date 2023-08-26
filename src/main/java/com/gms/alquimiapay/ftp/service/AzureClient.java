package com.gms.alquimiapay.ftp.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.ftp.payload.data.AzureFtpCredentials;
import com.gms.alquimiapay.ftp.payload.data.RemoteFileData;
import com.gms.alquimiapay.ftp.payload.response.RemoteFileListResponse;
import com.gms.alquimiapay.ftp.payload.response.RemoteFileUploadResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class AzureClient {

    private static AzureFtpCredentials azureFtpCredentials;

    public static void startFtpServer(AzureFtpCredentials credentials){
        azureFtpCredentials = credentials;
    }

    public static RemoteFileUploadResponse saveFile(@NotNull InputStream inputStream, @NonNull String directory, @NonNull String filenameWithExtension){
        BlobServiceClient serviceClient = getBlobServiceClientTokenCredential();
        BlobContainerClient containerClient = serviceClient.createBlobContainerIfNotExists(directory);
        BlobClient blobClient = containerClient.getBlobClient(filenameWithExtension);

        BlobHttpHeaders httpHeaders = new BlobHttpHeaders();
        httpHeaders.setContentType(getContentTypeFromFileNameWithExtension(filenameWithExtension));
        blobClient.setHttpHeaders(httpHeaders);

        RemoteFileUploadResponse response = new RemoteFileUploadResponse();
        String code;
        try{
            blobClient.upload(inputStream);
            inputStream.close();   // Close the input stream to enable deleting of file from local system.
            code = ResponseCode.SUCCESS;
            response.setResponseCode(code);
            response.setRemoteFileName(filenameWithExtension);
            response.setPublicLink(getPublicLinkOfRemoteFileInContainer(directory, filenameWithExtension));
            log.info("Successfully uploaded file to remote azure server");
        }catch (Exception e){
            e.printStackTrace();
            log.info("Exception while saving to remote azure server: {}", e.getMessage());

            code = ResponseCode.SYSTEM_ERROR;
            response.setResponseCode(code);
            response.setPublicLink("");
            response.setRemoteFileName(filenameWithExtension);
            try {
                inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public static RemoteFileListResponse getUploadedFiles(@NonNull List<String> directories){
        RemoteFileListResponse response = new RemoteFileListResponse();
        List<RemoteFileData> fileDataList = new ArrayList<>();
        String code = ResponseCode.SYSTEM_ERROR;

        try{
            for (String directory : directories) {
                BlobContainerClient containerClient = getBlobServiceClientTokenCredential().getBlobContainerClient(directory);
                if(!containerClient.getBlobContainerName().contains("$root")) {
                    PagedIterable<BlobItem> items = containerClient.listBlobs();
                    List<RemoteFileData> temp = items.stream().map(blobItem -> RemoteFileData.builder()
                            .remoteFileName(blobItem.getName())
                            .remoteParentDir(directory)
                            .publicLink(getPublicLinkOfRemoteFileInContainer(directory, blobItem.getName()))
                            .build()).collect(Collectors.toList());
                    fileDataList.addAll(temp);
                }
            }
            code = ResponseCode.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception while trying to get files from the azure remote server: {}", e.getMessage());
        }

        response.setResponseCode(code);
        response.setResponseData(fileDataList);
        return response;
    }

    private static String getPublicLinkOfRemoteFileInContainer(String container, String filenameWithExtension){
        return azureFtpCredentials.getEndpoint().concat(container).concat(StringValues.FORWARD_SLASH).concat(filenameWithExtension);
    }

    private static BlobServiceClient getBlobServiceClientTokenCredential(){
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(azureFtpCredentials.getAccountName(), azureFtpCredentials.getAccountKey());
        return new BlobServiceClientBuilder()
                .endpoint(azureFtpCredentials.getEndpoint())
                .credential(credential)
                .buildClient();
    }

    private static String getContentTypeFromFileNameWithExtension(String filenameWithExtension){
        String ext = filenameWithExtension.substring(filenameWithExtension.lastIndexOf(StringValues.DOT) + 1).toUpperCase().trim();
        String contentType;
        switch (ext){
            case "PNG" : contentType = MediaType.IMAGE_PNG_VALUE; break;
            case "JPG" : contentType = "image/jpg"; break;
            case "JPEG" : contentType = MediaType.IMAGE_JPEG_VALUE; break;
            case "GIF" : contentType = MediaType.IMAGE_GIF_VALUE; break;
            case "PDF" : contentType = MediaType.APPLICATION_PDF_VALUE; break;
            case "HTML" : contentType = MediaType.TEXT_HTML_VALUE; break;
            case "TXT" : contentType = MediaType.TEXT_PLAIN_VALUE; break;
            case "RTF" : contentType = "application/rtf"; break;
            case "XLSX" :
            case "XLS" :
                contentType = "application/vnd.ms-excel"; break;
            case "PPTX" :
            case "PPT" :
                contentType = "application/vnd.ms-powerpoint"; break;
            case "DOC" : contentType = "application/msword"; break;
            case "DOCX" : contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; break;
            default: contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return contentType;
    }
}
