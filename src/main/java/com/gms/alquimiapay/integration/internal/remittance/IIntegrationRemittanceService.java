package com.gms.alquimiapay.integration.internal.remittance;

import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import com.gms.alquimiapay.modules.user.model.GmsUser;
import com.gms.alquimiapay.payload.BaseResponse;

public interface IIntegrationRemittanceService
{
    BaseResponse processRemittanceSubmission(GmsUser sender, TransactionEntry transactionEntry);
}
