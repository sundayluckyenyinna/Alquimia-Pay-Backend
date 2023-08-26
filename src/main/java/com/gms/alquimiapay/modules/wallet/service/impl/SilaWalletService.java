package com.gms.alquimiapay.modules.wallet.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.QualifierValue;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.data.SilaWalletRequestData;
import com.gms.alquimiapay.integration.external.sila.dto.wallet.response.*;
import com.gms.alquimiapay.integration.external.sila.model.SilaUser;
import com.gms.alquimiapay.integration.external.sila.repository.SilaUserRepository;
import com.gms.alquimiapay.integration.internal.wallet.IIntegrationWalletService;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletData;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletOperationResult;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletUpdateChange;
import com.gms.alquimiapay.modules.wallet.payload.request.CreateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.UpdateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.WalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.response.CreateWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.MultipleWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.SingleWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.WalletUpdateResponsePayload;
import com.gms.alquimiapay.modules.wallet.repository.IGmsWalletCacheRepository;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.util.JwtUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(value = QualifierService.SILA_WALLET_SERVICE)
public class SilaWalletService implements IWalletService
{
    @Qualifier(QualifierValue.SILA_PARTY_WALLET_SERVICE)
    private final IIntegrationWalletService integrationWalletService;
    private final SilaUserRepository silaUserRepository;
    private final MessageProvider messageProvider;
    private final JwtUtil jwtUtil;
    private final IGmsWalletCacheRepository walletCacheRepository;

    private static final Gson JSON = new Gson();

    @Autowired
    public SilaWalletService(
            IIntegrationWalletService integrationWalletService,
            SilaUserRepository silaUserRepository,
            MessageProvider messageProvider,
            JwtUtil jwtUtil,
            IGmsWalletCacheRepository walletCacheRepository) {
        this.integrationWalletService = integrationWalletService;
        this.silaUserRepository = silaUserRepository;
        this.messageProvider = messageProvider;
        this.jwtUtil = jwtUtil;
        this.walletCacheRepository = walletCacheRepository;
    }

    @Override
    public CreateWalletResponsePayload processNewWalletCreation(String authToken, CreateWalletRequestPayload requestPayload){
        String token = cleanToken(authToken);
        String email = jwtUtil.getUserEmailFromJWTToken(token);
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        CreateWalletResponsePayload responsePayload = new CreateWalletResponsePayload();
        String code;
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        requestPayload.setUserHandle(silaUser.getSilaUserHandle());
        BaseResponse response = integrationWalletService.processNewWalletCreation(requestPayload);

        if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            // Cache the wallet created to database.
            SilaBaseWalletResponseDTO responseDTO = (SilaBaseWalletResponseDTO) response.getOtherDetails();
            GmsWalletCache wallet = new GmsWalletCache();
            wallet.setCreatedAt(LocalDateTime.now().toString());
            wallet.setUpdatedAt(LocalDateTime.now().toString());
            wallet.setWalletId(responseDTO.getWalletId());
            wallet.setWalletName(responseDTO.getWalletNickname());
            wallet.setVendor("SILA");
            wallet.setIsDefault(requestPayload.getIsDefault());
            wallet.setOwnerEmail(email);
            walletCacheRepository.saveAndFlush(wallet);

            WalletData walletData = JSON.fromJson(JSON.toJson(wallet), WalletData.class);

            responsePayload.setResponseCode(ResponseCode.SUCCESS);
            responsePayload.setResponseMessage(response.getResponseMessage());
            responsePayload.setResponseData(walletData);
            return responsePayload;
        }

        responsePayload.setResponseCode(response.getResponseCode());
        responsePayload.setResponseMessage(response.getResponseMessage());
        return responsePayload;
    }

    @Override
    public SingleWalletResponsePayload processFetchSingleWallet(String authToken, WalletRequestPayload requestPayload){
        String token = cleanToken(authToken);
        String email = jwtUtil.getUserEmailFromJWTToken(token);
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        SingleWalletResponsePayload responsePayload = new SingleWalletResponsePayload();
        String code;
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        requestPayload.setUserHandle(silaUser.getSilaUserHandle());
        BaseResponse response = integrationWalletService.processGetSingleWalletRequest(requestPayload);
        if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            SilaWalletResponseDTO responseDTO = (SilaWalletResponseDTO)response.getOtherDetails();
            SilaWalletRequestData silaWallet = responseDTO.getWallet();
            WalletData walletData = new WalletData();
            walletData.setWalletName(silaWallet.getNickname());
            walletData.setIsDefault(silaWallet.getDefaultWallet());
            walletData.setAvailableBalance(String.valueOf(responseDTO.getSilaAvailableBalance()));
            walletData.setPendingBalance(String.valueOf(responseDTO.getSilaPendingBalance()));
            walletData.setBlockchainNetwork(silaWallet.getBlockchainNetwork());
            walletData.setBlockchainAddress(silaWallet.getBlockchainAddress());
            walletData.setWalletId(silaWallet.getWalletId());
            walletData.setIsWhiteListed(responseDTO.getIsWhiteListed());

            GmsWalletCache cache = walletCacheRepository.findByWalletId(walletData.getWalletId());
            if(cache != null){
                cache.setIsWhiteListed(walletData.getIsWhiteListed());
                cache.setIsDefault(walletData.getIsDefault());
                cache.setAvailableBalance(walletData.getAvailableBalance());
                cache.setPendingBalance(walletData.getPendingBalance());
                cache.setUpdatedAt(LocalDateTime.now().toString());
                cache.setBlockchainAddress(walletData.getBlockchainAddress());
                cache.setBlockchainNetwork(walletData.getBlockchainNetwork());
                walletCacheRepository.saveAndFlush(cache);
            }

            responsePayload.setResponseCode(ResponseCode.SUCCESS);
            responsePayload.setResponseMessage(responseDTO.getMessage());
            responsePayload.setResponseData(walletData);
            return responsePayload;
        }

        responsePayload.setResponseCode(response.getResponseCode());
        responsePayload.setResponseMessage(response.getResponseMessage());
        return responsePayload;
    }

    @Override
    public MultipleWalletResponsePayload processFetchMultipleWallet(String authToken, Map<String, Object> filters){
        String token = cleanToken(authToken);
        String email = jwtUtil.getUserEmailFromJWTToken(token);
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        MultipleWalletResponsePayload responsePayload = new MultipleWalletResponsePayload();
        String code;
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        BaseResponse response = integrationWalletService.processGetMultipleWalletRequest(filters, silaUser.getSilaUserHandle());
        if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            SilaManyWalletResponseDTO responseDTO = (SilaManyWalletResponseDTO)response.getOtherDetails();
            List<SilaWalletResponseData> silaWallets = responseDTO.getWallets();

            List<WalletData> walletData = silaWallets.stream().map(sw -> {
                String walletId = sw.getWalletId();
                WalletRequestPayload requestPayload = new WalletRequestPayload();
                requestPayload.setUserHandle(silaUser.getSilaUserHandle());
                requestPayload.setWalletName(sw.getNickname());
                requestPayload.setWalletId(walletId);

                SingleWalletResponsePayload resPayload = this.processFetchSingleWallet(authToken, requestPayload);
                WalletData updatedData = resPayload.getResponseData();

                WalletData data = new WalletData();
                data.setIsWhiteListed(updatedData.getIsWhiteListed());
                data.setWalletId(walletId);
                data.setWalletName(sw.getNickname());
                data.setPendingBalance(updatedData.getPendingBalance());
                data.setAvailableBalance(updatedData.getAvailableBalance());
                data.setBlockchainAddress(updatedData.getBlockchainAddress());
                data.setBlockchainNetwork(updatedData.getBlockchainNetwork());
                data.setIsDefault(updatedData.getIsDefault());

                // Update the wallet cache in database.
                GmsWalletCache cache = walletCacheRepository.findByWalletId(walletId);
                if(cache != null){
                    cache.setIsWhiteListed(data.getIsWhiteListed());
                    cache.setIsDefault(data.getIsDefault());
                    cache.setAvailableBalance(data.getAvailableBalance());
                    cache.setPendingBalance(data.getPendingBalance());
                    cache.setUpdatedAt(LocalDateTime.now().toString());
                    cache.setBlockchainAddress(data.getBlockchainAddress());
                    cache.setBlockchainNetwork(data.getBlockchainNetwork());
                    walletCacheRepository.saveAndFlush(cache);
                }
                return data;
            }).collect(Collectors.toList());

            responsePayload.setResponseCode(ResponseCode.SUCCESS);
            responsePayload.setResponseMessage(response.getResponseMessage());
            responsePayload.setResponseData(walletData);
            return responsePayload;
        }

        responsePayload.setResponseCode(response.getResponseCode());
        responsePayload.setResponseMessage(response.getResponseMessage());
        return responsePayload;
    }

    @Override
    public WalletUpdateResponsePayload processUpdateWalletRequest(String authToken, UpdateWalletRequestPayload requestPayload){
        String token = cleanToken(authToken);
        String email = jwtUtil.getUserEmailFromJWTToken(token);
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        WalletUpdateResponsePayload responsePayload = new WalletUpdateResponsePayload();
        String code;
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        requestPayload.setUserHandle(silaUser.getSilaUserHandle());
        BaseResponse response = integrationWalletService.processUpdateWalletRequest(requestPayload);
        if(response.getResponseCode().equalsIgnoreCase(ResponseCode.SUCCESS)){
            SilaWalletUpdateResponseDTO responseDTO = (SilaWalletUpdateResponseDTO) response.getOtherDetails();

            List<WalletUpdateChange> changes = responseDTO.getChanges().stream().map(c -> {
                WalletUpdateChange change = new WalletUpdateChange();
                change.setAttribute(c.getAttribute());
                change.setOldValue(c.getOldValue());
                change.setNewValue(c.getNewValue());
                return change;
            }).collect(Collectors.toList());

            responsePayload.setResponseCode(ResponseCode.SUCCESS);
            responsePayload.setResponseMessage(response.getResponseMessage());
            responsePayload.setResponseData(changes);
            return responsePayload;
        }

        responsePayload.setResponseCode(response.getResponseCode());
        responsePayload.setResponseMessage(response.getResponseMessage());
        return responsePayload;
    }

    @Override
    public BaseResponse processDeleteWalletRequest(String authToken, String walletId){
        String token = cleanToken(authToken);
        String email = jwtUtil.getUserEmailFromJWTToken(token);
        SilaUser silaUser = silaUserRepository.findByGmsUserEmail(email);

        WalletUpdateResponsePayload responsePayload = new WalletUpdateResponsePayload();
        String code;
        if(silaUser == null || silaUser.getSilaUserHandle() == null || silaUser.getSilaUserHandle().isBlank() || silaUser.getSilaUserHandle().isEmpty()){
            code = ResponseCode.USER_HANDLE_NOT_FOUND;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        return integrationWalletService.processDeleteWalletRequest(walletId, silaUser.getSilaUserHandle());
    }

    @Override
    public WalletOperationResult processCreditWalletRequest(String amount, GmsWalletCache walletCache, WalletBalanceType balanceType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WalletOperationResult processDebitWalletRequest(String amount, GmsWalletCache walletCache, WalletBalanceType balanceType) {
        throw new UnsupportedOperationException();
    }

    private String cleanToken(String authToken){
        return authToken.startsWith(StringValues.AUTH_HEADER_BEARER_KEY) ? authToken.replace(StringValues.AUTH_HEADER_BEARER_KEY, StringValues.EMPTY_STRING).trim() : authToken.trim();
    }
}
