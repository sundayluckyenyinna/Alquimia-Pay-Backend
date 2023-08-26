package com.gms.alquimiapay.integration.internal.wallet;

import com.gms.alquimiapay.modules.wallet.payload.request.CreateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.UpdateWalletRequestPayload;
import com.gms.alquimiapay.modules.wallet.payload.request.WalletRequestPayload;
import com.gms.alquimiapay.payload.BaseResponse;

import java.util.Map;

public interface IIntegrationWalletService
{
    BaseResponse processNewWalletCreation(CreateWalletRequestPayload requestPayload);

    BaseResponse processGetSingleWalletRequest(WalletRequestPayload requestPayload);

    BaseResponse processGetMultipleWalletRequest(Map<String, Object> searchFilters, String userHandle);

    BaseResponse processUpdateWalletRequest(UpdateWalletRequestPayload requestPayload);

    BaseResponse processDeleteWalletRequest(String walletId, String userHandle);
}
