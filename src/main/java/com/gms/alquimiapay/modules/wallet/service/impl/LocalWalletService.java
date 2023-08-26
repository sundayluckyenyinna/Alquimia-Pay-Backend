package com.gms.alquimiapay.modules.wallet.service.impl;

import com.gms.alquimiapay.config.MessageProvider;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.constants.ResponseCode;
import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.modules.user.constants.UserType;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.modules.user.repository.IUserRepository;
import com.gms.alquimiapay.modules.user.validation.UserServiceValidator;
import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.constant.WalletOperation;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletData;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletOperationResult;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service(value = QualifierService.LOCAL_WALLET_SERVICE)
public class LocalWalletService implements IWalletService
{
    private final Environment env;
    private final IGmsWalletCacheRepository walletCacheRepository;
    private final JwtUtil jwtUtil;
    private final UserServiceValidator userValidator;
    private final MessageProvider messageProvider;
    private final IUserRepository userRepository;

    private static final Gson JSON = new Gson();

    @Autowired
    public LocalWalletService(
            Environment env,
            IGmsWalletCacheRepository walletCacheRepository,
            JwtUtil jwtUtil,
            UserServiceValidator userValidator,
            MessageProvider messageProvider,
            IUserRepository userRepository) {
        this.env = env;
        this.walletCacheRepository = walletCacheRepository;
        this.jwtUtil = jwtUtil;
        this.userValidator = userValidator;
        this.messageProvider = messageProvider;
        this.userRepository = userRepository;
    }


    @Override
    public CreateWalletResponsePayload processNewWalletCreation(String authToken, CreateWalletRequestPayload requestPayload) {
        CreateWalletResponsePayload responsePayload = new CreateWalletResponsePayload();
        String code;

        // Validate user existence.
        String email = jwtUtil.getUserEmailFromJWTToken(authToken);
        userValidator.validateUserExistValidationThrowException(email);

        GmsUser user = userRepository.findByEmailAddress(email);

        // Check if a wallet by this email exist.
        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(email);
        if(walletCache != null){
            code = ResponseCode.USER_ALREADY_LINKED_TO_LOCAL_WALLET;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        // Create local wallet and store in the database.
        walletCache = new GmsWalletCache();
        walletCache.setOwnerEmail(email);
        walletCache.setWalletId(this.generateLocalWalletId());
        walletCache.setWalletName(this.generateLocalWalletName(email));
        walletCache.setCreatedAt(LocalDateTime.now().toString());
        walletCache.setBlockchainNetwork(StringValues.EMPTY_STRING);
        walletCache.setBlockchainAddress(StringValues.EMPTY_STRING);
        walletCache.setUpdatedAt(walletCache.getCreatedAt());
        walletCache.setVendor(Vendor.LOCAL.name());
        walletCache.setPendingBalance("0.00");
        walletCache.setAvailableBalance("0.00");
        walletCache.setIsWhiteListed(true);
        walletCache.setIsDefault(Boolean.TRUE);
        walletCache.setStatus(ModelStatus.ACTIVE.name());

        if(user.getUserType().equalsIgnoreCase(UserType.INDIVIDUAL.name())){
            walletCache.setOwnerFullName(String.join(StringValues.SINGLE_EMPTY_SPACE, user.getLastName(), user.getFirstName(), user.getMiddleName()));
        }
        else if(user.getUserType().equalsIgnoreCase(UserType.BUSINESS.name())){
            walletCache.setOwnerFullName(user.getBusinessName());
        }

        GmsWalletCache createdWallet = walletCacheRepository.saveAndFlush(walletCache);
        WalletData walletData = JSON.fromJson(JSON.toJson(createdWallet), WalletData.class);

        // Return response to client.
        code = ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        responsePayload.setResponseData(walletData);
        return responsePayload;
    }

    @Override
    public SingleWalletResponsePayload processFetchSingleWallet(String authToken, WalletRequestPayload requestPayload) {
        SingleWalletResponsePayload responsePayload = new SingleWalletResponsePayload();
        String code;

        // Validate user existence.
        String email = jwtUtil.getUserEmailFromJWTToken(authToken);
        userValidator.validateUserExistValidationThrowException(email);

        GmsWalletCache walletCache = walletCacheRepository.findByOwnerEmail(email);
        if(walletCache == null){
            code = ResponseCode.NO_WALLET_RECORD_FOR_USER;
            responsePayload.setResponseCode(code);
            responsePayload.setResponseMessage(messageProvider.getMessage(code));
            return responsePayload;
        }

        WalletData walletData = JSON.fromJson(JSON.toJson(walletCache), WalletData.class);

        code= ResponseCode.SUCCESS;
        responsePayload.setResponseCode(code);
        responsePayload.setResponseMessage(messageProvider.getMessage(code));
        responsePayload.setResponseData(walletData);

        return responsePayload;
    }

    @Override
    public MultipleWalletResponsePayload processFetchMultipleWallet(String authToken, Map<String, Object> filters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WalletUpdateResponsePayload processUpdateWalletRequest(String authToken, UpdateWalletRequestPayload requestPayload) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseResponse processDeleteWalletRequest(String authToken, String walletId) {
        return null;
    }


    @Override
    public WalletOperationResult processCreditWalletRequest(String amount, GmsWalletCache walletCache, WalletBalanceType balanceType){
        return this.processLocalWalletOperation(walletCache, balanceType, WalletOperation.CREDIT, amount);
    }

    @Override
    public WalletOperationResult processDebitWalletRequest(String amount, GmsWalletCache walletCache, WalletBalanceType balanceType){
        return this.processLocalWalletOperation(walletCache, balanceType, WalletOperation.DEBIT, amount);
    }

    private WalletOperationResult processLocalWalletOperation(GmsWalletCache walletCache, WalletBalanceType balanceType, WalletOperation operation, String amount){
        WalletOperationResult result = new WalletOperationResult();
        String code = ResponseCode.UNABLE_TO_UPDATE_WALLET;

        result.setHasError(true);
        result.setResponseCode(code);
        result.setResponseMessage(messageProvider.getMessage(code));

        DecimalFormat df = new DecimalFormat("###,###,###.00");
        BigDecimal newBalance;
        if(balanceType == WalletBalanceType.AVAILABLE_BALANCE){
            String initialAvailableBalance = walletCache.getAvailableBalance();
            if(operation == WalletOperation.DEBIT){
                // Check if the wallet has sufficient amount for debit.
                int compared = new BigDecimal(amount).compareTo(new BigDecimal(initialAvailableBalance));
                if(compared > 0){
                    result.setResponseCode(ResponseCode.INSUFFICIENT_FUNDS);
                    result.setResponseMessage(messageProvider.getMessage(code));
                    return result;
                }
                newBalance = new BigDecimal(initialAvailableBalance)
                         .subtract(new BigDecimal(amount))
                         .setScale(2, RoundingMode.CEILING);
                 walletCache.setAvailableBalance(df.format(newBalance.doubleValue()));
                 walletCacheRepository.saveAndFlush(walletCache);

                 code = ResponseCode.SUCCESS;
                result.setHasError(false);
                result.setResponseCode(code);
                 result.setResponseMessage(messageProvider.getMessage(code));
                 return result;
             }
            else if(operation == WalletOperation.CREDIT){
                newBalance = new BigDecimal(initialAvailableBalance)
                        .add(new BigDecimal(amount))
                        .setScale(2, RoundingMode.CEILING);
                walletCache.setAvailableBalance(df.format(newBalance.doubleValue()));
                walletCacheRepository.saveAndFlush(walletCache);

                code = ResponseCode.SUCCESS;
                result.setHasError(false);
                result.setResponseCode(code);
                result.setResponseMessage(messageProvider.getMessage(code));
                return result;
            }
        }
        else if(balanceType == WalletBalanceType.PENDING_BALANCE){
            String initialPendingBalance = walletCache.getPendingBalance();
            if(operation == WalletOperation.DEBIT){
                // Check if the wallet has sufficient amount for debit.
                int compared = new BigDecimal(amount).compareTo(new BigDecimal(initialPendingBalance));
                if(compared > 0){
                    result.setResponseCode(ResponseCode.INSUFFICIENT_FUNDS);
                    result.setResponseMessage(messageProvider.getMessage(code));
                    return result;
                }
                newBalance = new BigDecimal(initialPendingBalance)
                        .subtract(new BigDecimal(amount))
                        .setScale(2, RoundingMode.CEILING);
                walletCache.setPendingBalance(df.format(newBalance.doubleValue()));
                walletCacheRepository.saveAndFlush(walletCache);

                code = ResponseCode.SUCCESS;
                result.setHasError(false);
                result.setResponseCode(code);
                result.setResponseMessage(messageProvider.getMessage(code));
                return result;
            }
            else if(operation == WalletOperation.CREDIT){
                newBalance = new BigDecimal(initialPendingBalance)
                        .add(new BigDecimal(amount))
                        .setScale(2, RoundingMode.CEILING);
                walletCache.setPendingBalance(df.format(newBalance.doubleValue()));
                walletCacheRepository.saveAndFlush(walletCache);

                code = ResponseCode.SUCCESS;
                result.setHasError(false);
                result.setResponseCode(code);
                result.setResponseMessage(messageProvider.getMessage(code));
                return result;
            }
        }

        log.info("Wallet update responseJson: {}", JSON.toJson(result));
        return result;
    }


    private String generateLocalWalletId(){
        int walletDigit = Integer.parseInt(Objects.requireNonNull(env.getProperty("gms.wallet.digit")));
        String nowString = String.valueOf(System.currentTimeMillis());
        String[] array = nowString.split(StringValues.EMPTY_STRING);
        Collections.reverse(Arrays.asList(array));
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < walletDigit; i++){
            builder.append(String.valueOf(array[i]));
        }
        return builder.toString();
    }

    private String generateLocalWalletName(String email){
        return Vendor.LOCAL.name().concat("_").concat(email);
    }
}
