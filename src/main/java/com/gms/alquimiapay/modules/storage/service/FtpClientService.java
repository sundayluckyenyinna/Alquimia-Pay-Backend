package com.gms.alquimiapay.modules.storage.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.ftp.payload.data.AzureFtpCredentials;
import com.gms.alquimiapay.ftp.payload.data.FtpCredentials;
import com.gms.alquimiapay.ftp.payload.response.RemoteFileListResponse;
import com.gms.alquimiapay.ftp.service.AzureClient;
import com.gms.alquimiapay.ftp.payload.response.RemoteFileUploadResponse;
import com.gms.alquimiapay.modules.storage.model.FileUpload;
import com.gms.alquimiapay.modules.storage.payload.FileUploadListRequestPayload;
import com.gms.alquimiapay.modules.storage.payload.FileUploadListResponsePayload;
import com.gms.alquimiapay.modules.storage.repository.IFileUploadRepository;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FtpClientService implements IFtpClientService
{

    @Value("${ftp.remote.host-name}")
    private String ftpHost;

    @Value("${ftp.remote.port}")
    private Integer ftpPort;

    @Value("${ftp.remote.username}")
    private String username;

    @Value("${ftp.remote.password}")
    private String password;

    @Value("${ftp.local.temp-dir}")
    private String localFileTempDir;

    @Value("${ftp.remote.azure.account-name}")
    private String accountName;

    @Value("${ftp.remote.azure.endpoint}")
    private String endpoint;

    @Value("${ftp.remote.azure.account-key}")
    private String accountKey;

    private final MessageProvider messageProvider;
    private final IFileUploadRepository fileUploadRepository;


    public FtpCredentials ftpCredentials(){
        FtpCredentials credentials = new FtpCredentials();
        credentials.setServerHostName(ftpHost);
        credentials.setServerPort(ftpPort);
        credentials.setUsername(username);
        credentials.setPassword(password);
        return credentials;
    }

    public AzureFtpCredentials azureFtpCredentials(){
        return AzureFtpCredentials.builder()
                .accountKey(accountKey)
                .accountName(accountName)
                .endpoint(endpoint)
                .build();
    }

    @Override
    public BaseResponse processFileStorage(String base64Content, FileUpload fileUpload, boolean saveToRemote) {
        BaseResponse response = new BaseResponse();
        String code = ResponseCode.SYSTEM_ERROR;

        try{
            // Try to save the file locally.
            String filenameWithExtension = fileUpload.getUniqueFileNameWithExtension();
            byte[] fileBytes = DatatypeConverter.parseBase64Binary(base64Content);
            String localFilePath = getLocalTempParentDir().getAbsolutePath().concat(File.separator).concat(filenameWithExtension);
            InputStream inputStream = new ByteArrayInputStream(fileBytes);

            // Try to save the file to the remote server if the option to save is true.
            if(saveToRemote){
                String remoteParentDirPath = fileUpload.getRemoteParentDirPath();
                RemoteFileUploadResponse remoteFileUploadResponse = this.processRemoteFileUpload(inputStream, remoteParentDirPath, filenameWithExtension);
                fileUpload.setPublic(true);
                if(remoteFileUploadResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
                    fileUpload.setPublicLink(remoteFileUploadResponse.getPublicLink());
                    fileUpload.setRemotelySaved(true);
                    fileUpload.setRemoteParentDirPath(remoteParentDirPath);
                    fileUploadRepository.saveAndFlush(fileUpload);
                }else{
                    fileUpload.setPublicLink(StringValues.EMPTY_STRING);
                    fileUpload.setRemotelySaved(false);
                    fileUpload.setRemoteParentDirPath(StringValues.EMPTY_STRING);
                    fileUploadRepository.saveAndFlush(fileUpload);
                    code = ResponseCode.FTP_FILE_CREATION_ERROR;
                    response.setResponseCode(code);
                    response.setResponseMessage(messageProvider.getMessage(code));
                    return response;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setResponseCode(code);
            response.setResponseMessage(messageProvider.getMessage(code));
            return response;
        }

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(messageProvider.getMessage(code));
        return response;
    }

    @Override
    public FileUploadListResponsePayload processFileUploadListForAdmin(FileUploadListRequestPayload requestPayload){
        FileUploadListResponsePayload responsePayload = new FileUploadListResponsePayload();
        String startLocalDateString = requestPayload.getStartDate();
        String endLocalDateString = requestPayload.getEndDate();
        if(endLocalDateString == null || endLocalDateString.isBlank() || endLocalDateString.isEmpty())
            endLocalDateString = startLocalDateString;


        LocalDate startLocalDate = LocalDate.parse(startLocalDateString);
        LocalDate endLocalDate = LocalDate.parse(endLocalDateString);

        Set<String> directories = getUniqueLocalDateYearMonthCombo(startLocalDateString, endLocalDateString).stream()
                .map(d -> requestPayload.getFolderPrefix().trim().concat(d))
                .collect(Collectors.toSet());
        List<FileUpload> fileUploads = new ArrayList<>();
        directories.forEach(directory -> {
            List<FileUpload> uploads = fileUploadRepository.findAllByRemoteParentDirPath(directory);
            System.out.println("Upload size: " + uploads.size());
            uploads = uploads.stream()
                    .filter(f -> {
                        LocalDate fileLocalDate = LocalDateTime.parse(f.getCreatedAt()).toLocalDate();
                        boolean b = (fileLocalDate.isEqual(startLocalDate) || fileLocalDate.isAfter(startLocalDate)) &&
                                (fileLocalDate.isEqual(endLocalDate) || fileLocalDate.isBefore(endLocalDate));
                        log.info("For Date: {} is GreaterThanOrEqualTo {} but lessThanOrEqualTo {} answer is: {}", fileLocalDate, startLocalDate, endLocalDate, b);
                        return b;
                    }).collect(Collectors.toList());
            fileUploads.addAll(uploads);
            uploads = null;  // Free up memory
        });

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setFileUploads(fileUploads);
        return responsePayload;
    }


    @Override
    public FileUploadListResponsePayload processFileUploadListForUser(String email, String startDate, String endDate, String folderPrefix){
        FileUploadListResponsePayload responsePayload = new FileUploadListResponsePayload();
        if(endDate == null || endDate.isBlank() || endDate.isEmpty()) {
            endDate = startDate;
        }

        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);

        Set<String> directories = getUniqueLocalDateYearMonthCombo(startDate, endDate).stream()
                .map(d -> folderPrefix.trim().concat(d))
                .collect(Collectors.toSet());
        List<FileUpload> fileUploads = new ArrayList<>();
        directories.forEach(directory -> {
            List<FileUpload> uploads = fileUploadRepository.findAllByRemoteParentDirPathAndOwnerEmail(directory, email).stream()
                    .filter(f -> {
                        LocalDate fileLocalDate = LocalDateTime.parse(f.getCreatedAt()).toLocalDate();
                        return (fileLocalDate.isEqual(startLocalDate) || fileLocalDate.isAfter(startLocalDate)) &&
                                (fileLocalDate.isEqual(endLocalDate) || fileLocalDate.isBefore(endLocalDate));
                    })
                    .collect(Collectors.toList());
            fileUploads.addAll(uploads);
            uploads = null;  // Free up memory
        });

        responsePayload.setResponseCode(ResponseCode.SUCCESS);
        responsePayload.setFileUploads(fileUploads);
        return responsePayload;
    }

    // GENERAL UTILITY
    @Override
    public FileUpload processSingleFileUploadById(String fileUUID){
        return fileUploadRepository.findByUuid(fileUUID);
    }

    private RemoteFileUploadResponse processRemoteFileUpload(InputStream inputStream, String remoteDirPath, String filenameWithExtension) throws IOException {
        AzureClient.startFtpServer(azureFtpCredentials());
        return AzureClient.saveFile(inputStream, remoteDirPath, filenameWithExtension);
    }

    private RemoteFileListResponse processRemoteFileListing(List<String> remoteDirPaths){
        AzureClient.startFtpServer(azureFtpCredentials());
        return AzureClient.getUploadedFiles(remoteDirPaths);
    }

    private static Set<String> getUniqueLocalDateYearMonthCombo(String startLocalDateString, String endLocalDateString){
        TreeSet<String> uniqueYearMonthCombo = new TreeSet<>();
        List<LocalDate> dates = getLocalDatesBetween(startLocalDateString, endLocalDateString);
        dates.forEach(d -> {
            String yearMonthCombo = String.join(StringValues.HYPHEN, String.valueOf(d.getYear()), getParseableDateElementFormat(d.getMonthValue())).trim();
            uniqueYearMonthCombo.add(yearMonthCombo);
        });

        return uniqueYearMonthCombo;
    }

    private static List<LocalDate> getLocalDatesBetween(String startLocalDateString, String endLocalDateString){
        LocalDate startLocalDate = LocalDate.parse(startLocalDateString);
        LocalDate endLocalDate = LocalDate.parse(endLocalDateString);
        int days  = startLocalDate.until(endLocalDate).getDays();
        List<LocalDate> iterableDates = new ArrayList<>();
        iterableDates.add(startLocalDate);
        for(int i = 1; i <= days; i++){
            iterableDates.add(startLocalDate.plusDays(i));
        }
        return iterableDates;
    }

    public static String getParseableDateElementFormat(int element){
        String str = String.valueOf(element).trim();
        return str.length() < 2 ? "0".concat(str) : str;
    }

    private File getLocalTempParentDir(){
        File tempDir = new File(localFileTempDir);
        if(tempDir.exists()) {
            return tempDir;
        }
        else{
            boolean isDirCreated = tempDir.mkdirs();
        }
        return tempDir;
    }

}
