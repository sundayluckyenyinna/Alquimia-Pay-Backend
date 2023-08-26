package com.gms.alquimiapay.integration.internal.account;

import com.gms.alquimiapay.model.MasterBankAccount;
import com.gms.alquimiapay.payload.BaseResponse;

public interface IAccountIntegrationService {

    BaseResponse processLinkWireAccount(MasterBankAccount masterBankAccount);

    BaseResponse processCreateCustomerVirtualAccountInstruction(String id);

    BaseResponse processGetSingleAccountStatusRequest(String id);
}
