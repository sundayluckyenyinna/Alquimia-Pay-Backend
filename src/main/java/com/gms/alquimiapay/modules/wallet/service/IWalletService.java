package com.gms.alquimiapay.modules.wallet.service;

import com.gms.alquimiapay.modules.wallet.constant.WalletBalanceType;
import com.gms.alquimiapay.modules.wallet.model.GmsWalletCache;
import com.gms.alquimiapay.modules.wallet.payload.data.WalletOperationResult;
import com.gms.alquimiapay.modules.wallet.payload.request.CreateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.UpdateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.WalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.response.CreateWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.MultipleWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.SingleWalletResponsePayload;
import com.gms.alquimiapay.modules.wallet.payload.response.WalletUpdateResponsePayload;
import com.gms.alquimiapay.payload.BaseResponse;

import java.util.Map;

public interface IWalletService
{

    CreateWalletResponsePayload processNewWalletCreation(String authToken, CreateWalletRequestPayload requestPayload);

    SingleWalletResponsePayload processFetchSingleWallet(String authToken, WalletRequestPayload requestPayload);

    MultipleWalletResponsePayload processFetchMultipleWallet(String authToken, Map<String, Object> filters);

    WalletUpdateResponsePayload processUpdateWalletRequest(String authToken, UpdateWalletRequestPayload requestPayload);

    BaseResponse processDeleteWalletRequest(String authToken, String walletId);

    WalletOperationResult processCreditWalletRequest(String amount, GmsWalletCache walletCache, WalletBalanceType balanceType);

    WalletOperationResult processDebitWalletRequest(String amount, GmsWalletCache walletCache, WalletBalanceType balanceType);
}
