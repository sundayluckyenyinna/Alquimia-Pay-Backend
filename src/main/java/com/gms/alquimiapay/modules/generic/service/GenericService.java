package com.gms.alquimiapay.modules.generic.service;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.model.LookupData;
import com.gms.alquimiapay.modules.generic.constant.HashAlgo;
import com.gms.alquimiapay.modules.generic.constant.LookupDataType;
import com.gms.alquimiapay.modules.generic.payload.LookupDataResponsePayload;
import com.gms.alquimiapay.modules.user.service.IUserIdentityService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
public class GenericService implements IGenericService
{

    private final IUserIdentityService userIdentityService;

    private final MessageProvider messageProvider;
    private static final Gson JSON = new Gson();


    @Autowired
    public GenericService(IUserIdentityService userIdentityService, MessageProvider messageProvider) {
        this.userIdentityService = userIdentityService;
        this.messageProvider = messageProvider;
    }


    @Override
    public LookupDataResponsePayload processLookupData(String lookupType) {

        if(lookupType.equalsIgnoreCase(LookupDataType.BUSINESS_TYPE.name())){
            return getBusinessTypeLookup();
        }
        else if(lookupType.equalsIgnoreCase(LookupDataType.BUSINESS_CATEGORY.name())){
            return getBusinessCategoryLookup();
        }
        else if(lookupType.equalsIgnoreCase(LookupDataType.DOCUMENT_TYPE.name())){
            return getDocumentLookup();
        }
        else if(lookupType.equalsIgnoreCase(LookupDataType.BUSINESS_ROLE.name())){
            return getBusinessRolesLookup();
        }

        String code = ResponseCode.NO_SUCH_LOOK_UP_DATA_TYPE;
        LookupDataResponsePayload responsePayload = new LookupDataResponsePayload();
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        return responsePayload;
    }


    private LookupDataResponsePayload getBusinessTypeLookup(){
        BaseResponse baseResponse = userIdentityService.processBusinessTypes();
        LookupDataResponsePayload responsePayload;
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            String lookupDataJson = baseResponse.getOtherDetailsJson();
            List<LookupData> lookupData = JSON.fromJson(lookupDataJson, new TypeToken<List<LookupData>>(){}.getType());
            responsePayload = new LookupDataResponsePayload();
            String code = ResponseCode.SUCCESS;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(baseResponse.getResponseMessage());
            responsePayload.setResponseData(lookupData);
            return responsePayload;
        }

        responsePayload = new LookupDataResponsePayload();
        responsePayload.setResponseCode(baseResponse.getResponseCode());
        responsePayload.setResponseMessage(baseResponse.getResponseMessage());
        return responsePayload;
    }

    private LookupDataResponsePayload getBusinessCategoryLookup(){
        BaseResponse baseResponse = userIdentityService.processBusinessCategories();
        LookupDataResponsePayload responsePayload;
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            String lookupDataJson = baseResponse.getOtherDetailsJson();
            List<LookupData> lookupData = JSON.fromJson(lookupDataJson, new TypeToken<List<LookupData>>(){}.getType());
            responsePayload = new LookupDataResponsePayload();
            String code = ResponseCode.SUCCESS;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(baseResponse.getResponseMessage());
            responsePayload.setResponseData(lookupData);
            return responsePayload;
        }

        responsePayload = new LookupDataResponsePayload();
        responsePayload.setResponseCode(baseResponse.getResponseCode());
        responsePayload.setResponseMessage(baseResponse.getResponseMessage());
        return responsePayload;
    }

    private LookupDataResponsePayload getDocumentLookup(){
        BaseResponse baseResponse = userIdentityService.processDocumentTypes();
        LookupDataResponsePayload responsePayload;
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            String lookupDataJson = baseResponse.getOtherDetailsJson();
            List<LookupData> lookupData = JSON.fromJson(lookupDataJson, new TypeToken<List<LookupData>>(){}.getType());
            responsePayload = new LookupDataResponsePayload();
            String code = ResponseCode.SUCCESS;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(baseResponse.getResponseMessage());
            responsePayload.setResponseData(lookupData);
            return responsePayload;
        }

        responsePayload = new LookupDataResponsePayload();
        responsePayload.setResponseCode(baseResponse.getResponseCode());
        responsePayload.setResponseMessage(baseResponse.getResponseMessage());
        return responsePayload;
    }

    private LookupDataResponsePayload getBusinessRolesLookup(){
        BaseResponse baseResponse = userIdentityService.processBusinessRoleRequest();
        LookupDataResponsePayload responsePayload;
        if(baseResponse.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            String lookupDataJson = baseResponse.getOtherDetailsJson();
            List<LookupData> lookupData = JSON.fromJson(lookupDataJson, new TypeToken<List<LookupData>>(){}.getType());
            responsePayload = new LookupDataResponsePayload();
            String code = ResponseCode.SUCCESS;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(baseResponse.getResponseMessage());
            responsePayload.setResponseData(lookupData);
            return responsePayload;
        }

        responsePayload = new LookupDataResponsePayload();
        responsePayload.setResponseCode(baseResponse.getResponseCode());
        responsePayload.setResponseMessage(baseResponse.getResponseMessage());
        return responsePayload;
    }

    @Override
    public String hashFileContent(File file, HashAlgo algo){
        if(algo.equals(HashAlgo.SHA_256)){
            ByteSource byteSource = com.google.common.io.Files.asByteSource(file);
            HashCode hc = null;
            try {
                hc = byteSource.hash(Hashing.sha256());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return hc.toString();
        }
        return StringValues.EMPTY_STRING;
    }

    @Override
    public String getBinaryDataFromFile(File file){
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            in.read(fileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder content = new StringBuilder();
        for(byte b : fileData) {
            content.append(getBits(b));
        }
        return content.toString();
    }

    public String getBits(byte b)
    {
        String result = "";
        for(int i = 0; i < 8; i++)
            result += (b & (1 << i)) == 0 ? "0" : "1";
        return result;
    }
}
