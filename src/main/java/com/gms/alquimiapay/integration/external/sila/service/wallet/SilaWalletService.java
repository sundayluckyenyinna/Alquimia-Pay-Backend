package com.gms.alquimiapay.integration.external.sila.service.wallet;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.integration.external.sila.constant.SilaApiPath;
import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletRequestData;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletSearchFilter;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.request.SilaSingleWalletRequestDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.request.SilaWalletCreationRequestDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.request.SilaWalletRequestDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.response.SilaBaseWalletResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.response.SilaManyWalletResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.response.SilaWalletResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.response.SilaWalletUpdateResponseDTO;
import com.gms.alquimiapay.integration.external.sila.repository.SilaUserRepository;
import com.gms.alquimiapay.integration.external.sila.service.ISilaGenericService;
import com.gms.alquimiapay.integration.internal.wallet.IIntegrationWalletService;
import com.gms.alquimiapay.modules.wallet.payload.request.CreateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.UpdateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.WalletRequestPayload;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.web.WebResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service(value = QualifierValue.SILA_PARTY_WALLET_SERVICE)
public class SilaWalletService implements IIntegrationWalletService
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
    public BaseResponse processNewWalletCreation(CreateWalletRequestPayload requestPayload) {
        BaseResponse response = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.REGISTER_WALLET);

        SilaWalletCreationRequestDTO requestDTO = new SilaWalletCreationRequestDTO();
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(requestPayload.getUserHandle());

        SilaWalletRequestData walletRequestData = new SilaWalletRequestData();
        walletRequestData.setNickname(requestPayload.getWalletName());
        walletRequestData.setDefaultWallet(requestPayload.getIsDefault());

        requestDTO.setHeader(header);
        requestDTO.setWallet(walletRequestData);
        requestDTO.setMessage(null);

        log.info("RequestJsonPost to create new wallet: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            response.setResponseMessage(webResponse.getErrorResponseJson());
            response.setOtherDetailsJson(webResponse.getErrorResponseJson());
            response.setOtherDetails(webResponse.getErrorResponseJson());
            return response;
        }

        log.info("Response from Sila to create new wallet: {}", webResponse.getSuccessResponseJson());

        SilaBaseWalletResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaBaseWalletResponseDTO.class);
        if(!responseDTO.isSuccess()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            response.setResponseMessage(responseDTO.getMessage());
            response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            response.setOtherDetails(responseDTO);
            return response;
        }

        String code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        response.setOtherDetails(responseDTO);
        return response;

    }

    @Override
    public BaseResponse processGetSingleWalletRequest(WalletRequestPayload requestPayload){
        BaseResponse response = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.GET_SINGLE_WALLET);

        SilaSingleWalletRequestDTO requestDTO = new SilaSingleWalletRequestDTO();
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(requestPayload.getUserHandle());

        requestDTO.setHeader(header);
        requestDTO.setNickname(requestPayload.getWalletName());
        requestDTO.setWalletId(requestPayload.getWalletId());
        requestDTO.setDefaultWallet(null);
        requestDTO.setMessage(null);

        log.info("RequestJsonPost to fetch single wallet: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            response.setResponseMessage(webResponse.getErrorResponseJson());
            response.setOtherDetailsJson(webResponse.getErrorResponseJson());
            response.setOtherDetails(webResponse.getErrorResponseJson());
            return response;
        }

        log.info("Response from sila to fetch single wallet: {}", webResponse.getSuccessResponseJson());

        SilaWalletResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaWalletResponseDTO.class);
        if(!responseDTO.isSuccess()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            response.setResponseMessage(responseDTO.getMessage());
            response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            response.setOtherDetails(responseDTO);
            return response;
        }

        String code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        response.setOtherDetails(responseDTO);
        return response;

    }

    @Override
    public BaseResponse processGetMultipleWalletRequest(Map<String, Object> searchFilters, String userHandle){
        BaseResponse response = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.GET_MANY_WALLET);

        SilaWalletRequestDTO requestDTO = new SilaWalletRequestDTO();
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(userHandle);

        SilaWalletSearchFilter searchFilter = new SilaWalletSearchFilter();
        Object pageNumber = searchFilters.get("pageNumber");
        Object pageSize = searchFilters.get("pageSize");
        Object ascending = searchFilters.get("ascending");
        Object nickname = searchFilters.get("walletName");
        Object walletId = searchFilters.get("walletId");

        searchFilter.setPageNumber(pageNumber == null ? null : (Integer) pageNumber);
        searchFilter.setPageSize(pageSize == null ? null : (Integer) pageSize);
        searchFilter.setAscending(ascending == null ? null : (Boolean) ascending);
        searchFilter.setNickname(nickname == null ? null : (String) nickname);
        searchFilter.setWalletId(walletId == null ? null : (String) walletId);

        requestDTO.setHeader(header);
        requestDTO.setSearchFilter(searchFilter);
        requestDTO.setMessage(null);

        log.info("RequestJson to fetch many wallet: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            response.setResponseMessage(webResponse.getErrorResponseJson());
            response.setOtherDetailsJson(webResponse.getErrorResponseJson());
            response.setOtherDetails(webResponse.getErrorResponseJson());
            return response;
        }

        log.info("Response from sila to fetch many wallet: {}", webResponse.getSuccessResponseJson());

        SilaManyWalletResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaManyWalletResponseDTO.class);
        if(!responseDTO.isSuccess()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            response.setResponseMessage(responseDTO.getMessage());
            response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            response.setOtherDetails(responseDTO);
            return response;
        }

        String code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        response.setOtherDetails(responseDTO);
        return response;
    }

    @Override
    public BaseResponse processUpdateWalletRequest(UpdateWalletRequestPayload requestPayload){
        BaseResponse response = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.UPDATE_WALLET);

        SilaSingleWalletRequestDTO requestDTO = new SilaSingleWalletRequestDTO();
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(requestPayload.getUserHandle());

        requestDTO.setHeader(header);
        requestDTO.setNickname(requestPayload.getWalletName());
        requestDTO.setWalletId(requestPayload.getWalletId());
        requestDTO.setDefaultWallet(requestPayload.getIsDefaultWallet());
        requestDTO.setMessage(null);

        log.info("RequestJsonPost to update wallet: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            response.setResponseMessage(webResponse.getErrorResponseJson());
            response.setOtherDetailsJson(webResponse.getErrorResponseJson());
            response.setOtherDetails(webResponse.getErrorResponseJson());
            return response;
        }

        log.info("Response from sila to update wallet: {}", webResponse.getSuccessResponseJson());

        SilaWalletUpdateResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), SilaWalletUpdateResponseDTO.class);
        if(!responseDTO.isSuccess()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            response.setResponseMessage(responseDTO.getMessage());
            response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            response.setOtherDetails(responseDTO);
            return response;
        }

        String code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        response.setOtherDetails(responseDTO);
        return response;

    }

    @Override
    public BaseResponse processDeleteWalletRequest(String walletId, String userHandle){
        BaseResponse response = new BaseResponse();
        String url = silaGenericService.resolveSilaUrl(SilaApiPath.DELETE_WALLET);

        SilaSingleWalletRequestDTO requestDTO = new SilaSingleWalletRequestDTO();
        SilaIdentityHeader header = silaGenericService.getSilaIdentityHeader();
        header.setUserHandle(userHandle);

        requestDTO.setHeader(header);
        requestDTO.setNickname(null);
        requestDTO.setWalletId(walletId);
        requestDTO.setDefaultWallet(null);
        requestDTO.setMessage(null);

        log.info("RequestJsonPost to delete wallet: {}", JSON.toJson(requestDTO));

        WebResponse webResponse = silaGenericService.postExchangeWithSila(url, requestDTO);
        if(webResponse.isHasConnectionError()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_UNAVAILABLE);
            response.setResponseMessage(webResponse.getErrorResponseJson());
            response.setOtherDetailsJson(webResponse.getErrorResponseJson());
            response.setOtherDetails(webResponse.getErrorResponseJson());
            return response;
        }

        log.info("Response from sila to delete wallet: {}", webResponse.getSuccessResponseJson());

        GenericSilaResponseDTO responseDTO = JSON.fromJson(webResponse.getSuccessResponseJson(), GenericSilaResponseDTO.class);
        if(!responseDTO.isSuccess()){
            response.setResponseCode(ResponseCode.THIRD_PARTY_SERVICE_FAILURE);
            response.setResponseMessage(responseDTO.getMessage());
            response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
            response.setOtherDetails(responseDTO);
            return response;
        }

        String code = ResponseCode.SUCCESS;
        response.setResponseCode(code);
        response.setResponseMessage(silaGenericService.resolveSilaMessage(messageProvider.getMessage(code), responseDTO.getMessage()));
        response.setOtherDetailsJson(webResponse.getSuccessResponseJson());
        response.setOtherDetails(responseDTO);
        return response;

    }
}
