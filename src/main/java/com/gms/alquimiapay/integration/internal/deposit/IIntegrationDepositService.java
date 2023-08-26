package com.gms.alquimiapay.integration.internal.deposit;

import com.gms.alquimiapay.payload.BaseResponse;

public interface IIntegrationDepositService
{
    BaseResponse processFetchAllBankDeposit(String startDateTime);
}
