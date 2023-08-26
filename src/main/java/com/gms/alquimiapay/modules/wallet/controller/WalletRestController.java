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

    @Operation(summary = "Fetch a single wallet for a user handle given a walletId", description = "This API fetch a single wallet for a user handle given a walletId")
    @PostMapping(value = WalletApiPath.GET_SINGLE_WALLET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SingleWalletResponsePayload> handleGetSingleWalletRequest(@RequestBody WalletRequestPayload requestPayload, @RequestHeader(StringValues.AUTH_HEADER_KEY) String authToken){
        genericValidator.doModelValidationThrowException(requestPayload);
        return ResponseEntity.ok(walletService.processFetchSingleWallet(authToken, requestPayload));
    }
}
