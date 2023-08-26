package com.gms.alquimiapay.modules.wallet.controller;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.modules.constant.QualifierService;
import com.gms.alquimiapay.modules.wallet.constant.WalletApiPath;
import com.gms.alquimiapay.modules.wallet.payload.request.CreateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.UpdateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.WalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.response.CreateWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.MultipleWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.SingleWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.WalletUpdateResponsePayload;
import com.gms.alquimiapay.modules.wallet.service.IWalletService;
import com.gms.alquimiapay.payload.BaseResponse;
import com.gms.alquimiapay.validation.GenericValidator;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(WalletApiPath.WALLET_BASE_URL)
@Api(tags = "Wallet Management Service", description = "Provides comprehensive functionalities for wallet management associated to a user handle")
public class WalletRestController
{
    private final GenericValidator genericValidator;
    private final IWalletService walletService;

    @Autowired
    public WalletRestController(
            GenericValidator genericValidator,
            @Qualifier(QualifierService.LOCAL_WALLET_SERVICE)
            IWalletService walletService) {
        this.genericValidator = genericValidator;
        this.walletService = walletService;
    }

    @Deprecated
    @Operation(summary = "Creates a new wallet for a given user handle", description = "This API creates a new wallet for a user handle. This wallet can then be selected and used for transacting", deprecated = true)
    @PostMapping(value = WalletApiPath.CREATE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateWalletResponsePayload> handleCreateWalletRequest(@RequestBody CreateWalletRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(walletService.processNewWalletCreation(authToken, requestPayload));
    }

    @Operation(summary = "Fetch a single wallet for a user handle given a walletId", description = "This API fetch a single wallet for a user handle given a walletId")
    @PostMapping(value = WalletApiPath.GET_SINGLE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleWalletResponsePayload> handleGetSingleWalletRequest(@RequestBody WalletRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(walletService.processFetchSingleWallet(authToken, requestPayload));
    }

    @Deprecated
    @Operation(summary = "Fetch all wallets associated with a user handle with dynamic filters", description = "Fetch all wallets associated with a user handle with dynamic wallets. It should be noted that none of the dynamic parameters are required but can be passed to enable concisely and pagination. If none of the filter is provided, the default values are used.", deprecated = true)
    @GetMapping(value = WalletApiPath.GET_ALL_WALLET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultipleWalletResponsePayload> handleGetMultipleWalletRequest(
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "ascending", required = false) Boolean ascending,
            @RequestParam(value = "walletName", required = false) String walletName,
            @RequestParam(value = "walletId", required = false) String walletId,
            @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken
    ){
        Map<String, Object> filters = new HashMap<>();
        filters.put("pageNumber", pageNumber);
        filters.put("pageSize", pageSize);
        filters.put("ascending", ascending);
        filters.put("walletName", walletName);
        filters.put("walletId", walletId);

        return ResponseEntity.ok(walletService.processFetchMultipleWallet(authToken, filters));
    }

    @Deprecated
    @Operation(summary = "Update a single wallet for a given user handle", description = "This API updates a single wallet for a given user handle", deprecated = true)
    @PostMapping(value = WalletApiPath.UPDATE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WalletUpdateResponsePayload> handleUpdateWalletRequest(@RequestBody UpdateWalletRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(walletService.processUpdateWalletRequest(authToken, requestPayload));
    }

    @Deprecated
    @Operation(summary = "Delete a single wallet for a given user handle", description = "This API deletes a single wallet for a given user handle", deprecated = true)
    @DeleteMapping(value = WalletApiPath.DELETE_WALLET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> handleDeleteWalletRequest(@RequestParam("walletId") String walletId, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        return ResponseEntity.ok(walletService.processDeleteWalletRequest(authToken, walletId));
    }
}
