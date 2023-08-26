package com.gms.alquimiapay.integration.external.sila.service.identity_reg;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.integration.external.sila.constant.IdentityAlias;
import com.gms.alquimiapay.integration.external.sila.constant.SilaApiPath;
import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaBusinessHeader;
import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.gms.alquimiapay.integration.external.sila.dto.identity.request.*;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.*;
import com.gms.alquimiapay.integration.external.sila.dto.identity.response.*;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.response.UnlinkBusinessMemberResponseDTO;
import com.gms.alquimiapay.integration.external.sila.model.SilaUser;
import com.gms.alquimiapay.integration.external.sila.repository.SilaUserRepository;
import com.gms.alquimiapay.integration.external.sila.service.ISilaGenericService;
import com.gms.alquimiapay.integration.internal.identity.IIntegrationIdentityService;
import com.gms.alquimiapay.model.LookupData;
import com.gms.alquimiapay.modules.kyc.model.UserUploadDocument;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service(value = QualifierValue.SILA_PARTY_IDENTITY_SERVICE)
public class SilaUserIdentityService implements IIntegrationIdentityService
{

    @Autowired
    private ISilaGenericService silaGenericService;
    @Autowired
    private SilaUserRepository silaUserRepository;

    @Value("${third-party.sila.app-handle}")
    private String silaAppHandle;
    @Value("${third-party.sila.version}")
    private String silaVersion;
    @Value("${third-party.sila.check-handle.message}")
    private String checkUserHandleMessage;
    @Value("${third-party.sila.check-user-handle}")
    private String checkUserHandleUrl;
    @Autowired
    private MessageProvider messageProvider;

    private static final Gson JSON = new Gson();


    @Override
    public BaseResponse checkUserHandle(String userHandle) {

        SilaBaseRequestDTO requestDTO = new SilaBaseRequestDTO();
        SilaIdentityHeader silaIdentityHeader = silaGenericService.getSilaIdentityHeader();
        silaIdentityHeader.setUserHandle(userHandle);
        requestDTO.setHeader(silaIdentityHeader);
        requestDTO.setMessage(checkUserHandleMessage);

        System.out.println("RequestDTO: {}"+ JSON.toJson(requestDTO));
        WebResponse webResponsePost = silaGenericService.postExchangeWithSila(checkUserHandleUrl, requestDTO);
        BaseResponse response = new BaseResponse();
        String code;
        if(webResponsePost.isHasConnectionError()){
            log.info("Connection Error to check user handle: {}", webResponsePost.getErrorResponseJson());
            return JSON.fromJson(webResponsePost.getErrorResponseJson(), BaseResponse.class);
        }

        GenericSilaResponseDTO genericSilaResponseDTO = JSON.fromJson(webResponsePost.getSuccessResponseJson(), GenericSilaResponseDTO.class);
        if(!genericSilaResponseDTO.isSuccess()){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            response.setResponseCode(code);
            response.setResponseMessage(silaGenericService.resolveSilaMessage(genericSilaResponseDTO.getMessage(), webResponsePost.getSuccessResponseJson()));
            response.setOtherDetailsJson(webResponsePost.getSuccessResponseJson());

            log.info("Error to check user handle: {}", genericSilaResponseDTO.getMessage());
            return response;
        }

        code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), genericSilaResponseDTO.getMessage()));

        log.info("Successful response to check user handle: {}", webResponsePost.getSuccessResponseJson());
        return response;
    }

    @Override
    public BaseResponse processUserRegistrationToThirdParty(GmsUser user){
        BaseResponse responsePayload = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.USER_REGISTRATION);
        SilaUserRegistrationRequestDTO requestDTO = buildSilaUser(user);
        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(webResponse.getErrorResponseJson());
            responsePayload.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return responsePayload;
        }

        GenericSilaResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), GenericSilaResponseDTO.class);
        if(!responseDTO.isSuccess()){
            responsePayload.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            responsePayload.setResponseMessage(silaGenericService.resolveSilaMessage(responseDTO.getMessage(), webResponse.getSuccessResponseJson()));
            return responsePayload;
        }

        // Send message asynchronously to the client for a successful kyc onboarding with Sila.

        String code = ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        responsePayload.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return responsePayload;
    }

    @Override
    public BaseResponse processBusinessTypesRequest(){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.SILA_BUSINESS_TYPES);
        SilaBaseRequestDTO requestDTO = new SilaBaseRequestDTO();
        requestDTO.setHeader(silaGenericService.getSilaIdentityHeader());
        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        SilaBusinessTypeResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaBusinessTypeResponseDTO.class);
        log.info("ResponseJSon: {}", JSON.toJson(responseDTO));

        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(responseDTO.getMessage(), webResponse.getSuccessResponseJson()));
            baseResponse.setOtherDetailsJson(JSON.toJson(responseDTO));
            return baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        List<SilaBusinessTypeResponseData> data = responseDTO.getBusinessTypes();
        List<LookupData> lookupData = data.stream()
                .map(d -> {
                    LookupData ld = new LookupData();
                    ld.setId(d.getUuid());
                    ld.setName(d.getName());
                    ld.setLabel(d.getLabel());
                    ld.setRequiresCertification(d.isRequiresCertification());
                    return ld;
                })
                .collect(Collectors.toList());

        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(lookupData);
        baseResponse.setOtherDetailsJson(JSON.toJson(lookupData));
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessCategoriesRequest(){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.SILA_BUSINESS_CATEGORIES);
        SilaBaseRequestDTO requestDTO = new SilaBaseRequestDTO();
        requestDTO.setHeader(silaGenericService.getSilaIdentityHeader());
        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        System.out.println("Res: " +  webResponse.getSuccessResponseJson());
        SilaBusinessCategoryResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaBusinessCategoryResponseDTO.class);

        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(responseDTO.getMessage(), webResponse.getSuccessResponseJson()));
            baseResponse.setOtherDetailsJson(JSON.toJson(responseDTO));
            return baseResponse;
        }

        List<BusinessSubCategory> subCategories = new ArrayList<>();
        Object businessCategories = responseDTO.getBusinessSuperCategories();
        Map map = JSON.fromJson(JSON.toJson(businessCategories), Map.class);
        Set<String> superCategoriesKeySet = map.keySet();
        superCategoriesKeySet.forEach(key -> {
            Object subCategoryList = map.get(key);
            String subCategoryListJson = JSON.toJson(subCategoryList);
            List<BusinessSubCategory> list = JSON.fromJson(subCategoryListJson, new TypeToken<List<BusinessSubCategory>>(){}.getType());
            subCategories.addAll(list);
        });

        List<LookupData> lookupData = subCategories.stream()
                .map(sc -> {
                    LookupData data = new LookupData();
                    data.setId(String.valueOf(sc.getCode()));
                    data.setName(sc.getSubcategory());
                    data.setLabel(sc.getSubcategory());
                    data.setRequiresCertification(false);
                    return data;
                })
                .collect(Collectors.toList());

        String code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(lookupData);
        baseResponse.setOtherDetailsJson(JSON.toJson(lookupData));
        return baseResponse;
    }

    @Override
    public BaseResponse processDocumentTypesRequest(){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.SILA_DOCUMENT_TYPES);
        SilaBaseRequestDTO requestDTO = new SilaBaseRequestDTO();
        requestDTO.setHeader(silaGenericService.getSilaIdentityHeader());
        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        SilaDocumentTypeResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaDocumentTypeResponseDTO.class);
        log.info("ResponseJSon: {}", JSON.toJson(responseDTO));

        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(responseDTO.getMessage(), webResponse.getSuccessResponseJson()));
            baseResponse.setOtherDetailsJson(JSON.toJson(responseDTO));
            return baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        List<SilaDocumentType> data = responseDTO.getDocumentTypes();
        List<LookupData> lookupData = data.stream()
                .map(d -> {
                    LookupData ld = new LookupData();
                    ld.setId(d.getName());
                    ld.setName(d.getName());
                    ld.setLabel(d.getIdentityType());
                    ld.setRequiresCertification(true);
                    return ld;
                })
                .collect(Collectors.toList());

        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(lookupData);
        baseResponse.setOtherDetailsJson(JSON.toJson(lookupData));
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessRolesRequest(){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.SILA_BUSINESS_ROLES);
        SilaBaseRequestDTO requestDTO = new SilaBaseRequestDTO();
        requestDTO.setHeader(silaGenericService.getSilaIdentityHeader());

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        SilaBusinessRolesResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaBusinessRolesResponseDTO.class);
        log.info("ResponseJSon: {}", JSON.toJson(responseDTO));

        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(responseDTO.getMessage(), webResponse.getSuccessResponseJson()));
            baseResponse.setOtherDetailsJson(JSON.toJson(responseDTO));
            return baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        List<SilaBusinessRoleData> data = responseDTO.getBusinessRoles();
        List<LookupData> lookupData = data.stream()
                .map(d -> {
                    LookupData ld = new LookupData();
                    ld.setId(d.getUuid());
                    ld.setName(d.getName());
                    ld.setLabel(d.getLabel());
                    ld.setRequiresCertification(true);
                    return ld;
                })
                .collect(Collectors.toList());

        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(lookupData);
        baseResponse.setOtherDetailsJson(JSON.toJson(lookupData));
        return baseResponse;
    }

    @Override
    public BaseResponse processKYCRequest(GmsUser user){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.REQUEST_KYC);

        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(user.getEmailAddress());
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();

        String userHandle = silaUser.getSilaUserHandle();
        if(userHandle == null || userHandle.isEmpty() || userHandle.isBlank()){
            String code = ResponseCode.USER_HANDLE_NOT_FOUND;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        header.setUserHandle(userHandle);

        KycRequestDTO requestDTO = new KycRequestDTO();
        requestDTO.setHeader(header);
        if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())) {
            requestDTO.setKycLevel("DOC_KYC");
        }

        requestDTO.setMessage("header_msg");
        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        KycResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), KycResponseDTO.class);
        if(!responseDTO.isSuccess()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return  baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processKycRequestChecking(GmsUser user){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.CHECK_KYC);

        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(user.getEmailAddress());
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();

        String userHandle = silaUser.getSilaUserHandle();
        if(userHandle == null || userHandle.isEmpty() || userHandle.isBlank()){
            String code = ResponseCode.USER_HANDLE_NOT_FOUND;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        header.setUserHandle(userHandle);

        KycCheckRequestDTO requestDTO = new KycCheckRequestDTO();
        requestDTO.setHeader(header);
        requestDTO.setMessage("header_msg");

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()) {
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        KycCheckResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), KycCheckResponseDTO.class);
        if(!responseDTO.isSuccess()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetails(responseDTO);
            return  baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processSingleKycDocumentUpload(UserUploadDocument document){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.KYC_DOC_UPLOAD);

        String email = document.getUserEmail();
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        String userHandle = silaUser.getSilaUserHandle();
        if(userHandle == null || userHandle.isEmpty() || userHandle.isBlank()){
            String code = ResponseCode.USER_HANDLE_NOT_FOUND;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(userHandle);

        Map<String, Object> form = new HashMap<>();
        Map<String, Object> files = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        files.put("file", new File(document.getAbsolutePath()));

        data.put("header", JSON.fromJson(JSON.toJson(header), Map.class));
        data.put("message", "header_msg");
        data.put("name", document.getName());
        data.put("filename", document.getFileName());
        data.put("hash", document.getHash());
        data.put("mime_type", document.getMimeType());
        data.put("document_type", document.getDocumentType());
        data.put("identity_type", document.getIdentityType());
        data.put("description", document.getFileDescription());

        form.put("files", files);
        form.put("file", new File(document.getAbsolutePath()));
        form.put("data", JSON.toJson(data));

        File file = new File(document.getAbsolutePath());
        try {
            System.out.println(Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WebResponse webResponse = silaGenericService.postFormExchangeWithSilaRestTemplate(url, form);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        KycDocumentUploadResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), KycDocumentUploadResponseDTO.class);
        if(!responseDTO.isSuccess()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return  baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }


    @Override
    public BaseResponse processMultipleKycDocumentUpload(List<UserUploadDocument> documents){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.KYC_DOC_UPLOAD);

        String email = documents.get(0).getUserEmail();
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        String userHandle = silaUser.getSilaUserHandle();
        if(userHandle == null || userHandle.isEmpty() || userHandle.isBlank()){
            String code = ResponseCode.USER_HANDLE_NOT_FOUND;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(userHandle);

        Map<String, Object> form = new HashMap<>();
        Map<String, Object> files = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> fileMetadata = new HashMap<>();

        AtomicInteger atomicInteger = new AtomicInteger();
        documents.forEach(doc -> {
            SilaFileMetaData metaData = new SilaFileMetaData();
            metaData.setName(doc.getName());
            metaData.setFileName(doc.getFileName());
            metaData.setMimeType(doc.getMimeType());
            metaData.setHash(doc.getHash());
            metaData.setDocumentType(doc.getDocumentType());
            metaData.setDescription(doc.getFileDescription());

            String fileKey = "file".concat(String.valueOf(atomicInteger.incrementAndGet()));
            files.put(fileKey, doc.getFileContent().getBytes(StandardCharsets.UTF_8));
            fileMetadata.put(fileKey, metaData);
        });

        data.put("header", header);
        data.put("file_meta_data", fileMetadata);

        form.put("files", files);
        form.put("data", data);

        WebResponse webResponse = silaGenericService.postFormExchangeWithSila(url, form);
        if(webResponse.isHasConnectionError()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            return baseResponse;
        }

        GenericSilaResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), GenericSilaResponseDTO.class);
        if(!responseDTO.isSuccess()){
            String code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return  baseResponse;
        }

        String code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseCode(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessAdministratorRegistration(String businessHandle, String adminHandle){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.LINK_BUSINESS_MEMBER);

        SilaBusinessHeader businessHeader = silaGenericService.getSilaBusinessHeader();
        businessHeader.setUserHandle(adminHandle);
        businessHeader.setBusinessHandle(businessHandle);

        LinkBusinessMemberRequestDTO requestDTO = new LinkBusinessMemberRequestDTO();
        requestDTO.setHeader(businessHeader);
        requestDTO.setRole("administrator");
        requestDTO.setMemberHandle(null);
        requestDTO.setOwnershipStake(null);

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            baseResponse.setResponseMessage(messageProvider.getMessage(baseResponse.getResponseCode()));
            return baseResponse;
        }

        log.info("Add Administrator officer requestJsonPost: {}", JSON.toJson(requestDTO));

        KycResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), KycResponseDTO.class);
        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetails(responseDTO);
            return baseResponse;
        }

        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(ResponseCode.SUCCESS), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessControllingOfficerRegistration(String businessHandle, String officerHandle, String adminHandle){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.LINK_BUSINESS_MEMBER);

        SilaBusinessHeader businessHeader = silaGenericService.getSilaBusinessHeader();
        businessHeader.setUserHandle(adminHandle);
        businessHeader.setBusinessHandle(businessHandle);

        LinkBusinessMemberRequestDTO requestDTO = new LinkBusinessMemberRequestDTO();
        requestDTO.setHeader(businessHeader);
        requestDTO.setRole("controlling_officer");
        requestDTO.setMemberHandle(officerHandle);
        requestDTO.setOwnershipStake(null);

        log.info("Add Controlling officer requestJsonPost: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            baseResponse.setResponseMessage(messageProvider.getMessage(baseResponse.getResponseCode()));
            return baseResponse;
        }

        KycResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), KycResponseDTO.class);
        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetails(responseDTO);
            return baseResponse;
        }

        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(ResponseCode.SUCCESS), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessBeneficialOwnerRegistration(String businessHandle, String officerHandle, String adminHandle, String details, double ownerStake){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.LINK_BUSINESS_MEMBER);

        SilaBusinessHeader businessHeader = silaGenericService.getSilaBusinessHeader();
        businessHeader.setUserHandle(officerHandle);
        businessHeader.setBusinessHandle(businessHandle);

        LinkBusinessMemberRequestDTO requestDTO = new LinkBusinessMemberRequestDTO();
        requestDTO.setHeader(businessHeader);
        requestDTO.setRole("beneficial_owner");
        requestDTO.setDetails(details);
        requestDTO.setOwnershipStake(ownerStake);

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            baseResponse.setResponseMessage(messageProvider.getMessage(baseResponse.getResponseCode()));
            return baseResponse;
        }

        log.info("Add Business officer requestJsonPost: {}", JSON.toJson(requestDTO));

        KycResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), KycResponseDTO.class);
        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetails(responseDTO);
            return baseResponse;
        }

        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(ResponseCode.SUCCESS), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessMemberUnlinking(String userHandle, String businessHandle, String role){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.UNLINK_BUSINESS_MEMBER);

        SilaBusinessHeader businessHeader = silaGenericService.getSilaBusinessHeader();
        businessHeader.setUserHandle(userHandle);
        businessHeader.setBusinessHandle(businessHandle);

        UnlinkBusinessMemberRequestDTO requestDTO = new UnlinkBusinessMemberRequestDTO();
        requestDTO.setHeader(businessHeader);
        requestDTO.setRole(role);

        log.info("RequestJsonPost to unlink a business member: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            baseResponse.setResponseMessage(messageProvider.getMessage(baseResponse.getResponseCode()));
            return baseResponse;
        }

        log.info("Response from Sila to unlink business member: {}", webResponse.getSuccessResponseJson());

        UnlinkBusinessMemberResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), UnlinkBusinessMemberResponseDTO.class);
        if(!responseDTO.isSuccess()){
            baseResponse.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetails(responseDTO);
            return baseResponse;
        }

        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(ResponseCode.SUCCESS), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        return baseResponse;
    }

    @Override
    public BaseResponse processGetUserEntity(String userHandle){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.GET_ENTITY);

        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(userHandle);

        SilaBaseRequestDTO requestDTO = new SilaBaseRequestDTO();
        requestDTO.setHeader(header);

        log.info("Get user entity requestJson: {}", JSON.toJson(requestDTO));
        String code;
        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        SilaEntityResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaEntityResponseDTO.class);
        if(!responseDTO.isSuccess()){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            baseResponse.setOtherDetails(responseDTO);
            return baseResponse;
        }

        code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetails(responseDTO);
        baseResponse.setOtherDetailsJson(JSON.toJson(requestDTO));
        return baseResponse;
    }

    @Override
    public BaseResponse processBeneficialOwnerCertification(String ownerHandle, String adminHandle, String businessHandle, String certToken){
        BaseResponse baseResponse = new BaseResponse();
        String code;
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.CERTIFY_BENEFICIAL_OWNER);

        SilaBusinessHeader header = silaGenericService.getSilaBusinessHeader();
        header.setUserHandle(adminHandle);
        header.setBusinessHandle(businessHandle);

        BusinessOwnerCertificationRequestDTO requestDTO = new BusinessOwnerCertificationRequestDTO();
        requestDTO.setHeader(header);
        requestDTO.setMemberHandle(ownerHandle);
        requestDTO.setCertificationToken(certToken);

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            baseResponse.setOtherDetailsJson(webResponse.getErrorResponseJson());
        }

        GenericSilaResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), GenericSilaResponseDTO.class);
        if(!responseDTO.isSuccess()){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        baseResponse.setOtherDetails(responseDTO);
        return baseResponse;
    }

    @Override
    public BaseResponse processBusinessCertification(String adminHandle, String businessHandle){
        BaseResponse baseResponse = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.CERTIFY_BUSINESS);
        String code;

        SilaBusinessHeader header = silaGenericService.getSilaBusinessHeader();
        header.setUserHandle(adminHandle);
        header.setBusinessHandle(businessHandle);

        SilaBaseBusinessHeaderRequestDTO requestDTO = new SilaBaseBusinessHeaderRequestDTO();
        requestDTO.setHeader(header);

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            code = ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(messageProvider.getMessage(code));
            baseResponse.setOtherDetailsJson(webResponse.getErrorResponseJson());
        }

        GenericSilaResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), GenericSilaResponseDTO.class);
        if(!responseDTO.isSuccess()){
            code = ResponseCode.THIRD_PARTY_SERVICE_FAILURE;
            baseResponse.setResponseCode(code);
            baseResponse.setResponseMessage(responseDTO.getMessage());
            baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            return baseResponse;
        }

        code = ResponseCode.SUCCESS;
        baseResponse.setResponseCode(code);
        baseResponse.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        baseResponse.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        baseResponse.setOtherDetails(responseDTO);
        return baseResponse;
    }

    private SilaUserRegistrationRequestDTO buildSilaUser(GmsUser gmsUser){
        SilaUserRegistrationRequestDTO req = new SilaUserRegistrationRequestDTO();

        // Get the Sila user.
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(gmsUser.getEmailAddress());

        // Create the address entity
        SilaAddress address = new SilaAddress();
        address.setAddressAlias(gmsUser.getAddressAlias());
        address.setCity(gmsUser.getCity());
        address.setCountry(gmsUser.getCountry());
        address.setStreetAddress1(gmsUser.getAddress());
        address.setState(gmsUser.getState());
        address.setPostalCode(gmsUser.getZipCode());

        // Create the SilaContact
        SilaContact contact = new SilaContact();
        contact.setContactAlias(gmsUser.getContactAlias() == null ? StringValues.EMPTY_STRING : gmsUser.getContactAlias());
        contact.setPhone(gmsUser.getMobileNumber());
        contact.setEmail(gmsUser.getEmailAddress());

        // Create SilaIdentity
        SilaIdentity identity = new SilaIdentity();
        if(gmsUser.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.toString())){
            identity.setIdentityAlias(IdentityAlias.SSN.name());
            identity.setIdentityValue(gmsUser.getSsn());
        }
        if(gmsUser.getUserType().equalsIgnoreCase(UserType.BUSINESS.toString())){
            identity.setIdentityAlias(IdentityAlias.EIN.name());
            identity.setIdentityValue(gmsUser.getEmployerIdentificationNumber());
        }

        // Create the user entity
        SilaUserEntity silaUserEntity = new SilaUserEntity();
        silaUserEntity.setBirthdate(gmsUser.getDateOfBirth());
        if(gmsUser.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())) {
            silaUserEntity.setFirstName(gmsUser.getFirstName());
            silaUserEntity.setLastName(gmsUser.getLastName());
            silaUserEntity.setType(UserType.INDIVIDUAL.name().toLowerCase());
        }
        else {
            silaUserEntity.setEntityName(gmsUser.getBusinessName());
            silaUserEntity.setType(UserType.BUSINESS.name().toLowerCase());
            silaUserEntity.setBusinessType(gmsUser.getBusinessType());
            silaUserEntity.setBusinessTypeUUID(gmsUser.getBusinessTypeUUID());
            silaUserEntity.setNaicsCode(gmsUser.getBusinessCategoryCode());
            silaUserEntity.setDoingBusinessAs(gmsUser.getBusinessNickName());
            silaUserEntity.setBusinessWebsite(gmsUser.getBusinessWebsite());

        }

        SilaIdentityHeader silaIdentityHeader = silaGenericService.getSilaIdentityHeader();
        silaIdentityHeader.setUserHandle(silaUser.getSilaUserHandle());
        req.setHeader(silaIdentityHeader);
        req.setMessage("entity_msg");
        req.setAddress(address);
        req.setContact(contact);
        req.setIdentity(identity);
        req.setEntity(silaUserEntity);

        return req;
    }

}
