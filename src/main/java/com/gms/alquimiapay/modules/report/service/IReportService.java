package com.gms.alquimiapay.modules.report.service;

import com.gms.alquimiapay.modules.report.payload.request.ReportRequestPayload;
import com.gms.alquimiapay.modules.report.payload.response.AlquimiaRemittanceResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.DepositsResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.TransactionEntriesResponsePayload;
import com.gms.alquimiapay.modules.report.payload.response.UsersResponsePayload;

public interface IReportService {

    UsersResponsePayload getSignupUsers(ReportRequestPayload requestPayload);

    TransactionEntriesResponsePayload getTransactionEntries(ReportRequestPayload requestPayload);

    AlquimiaRemittanceResponsePayload getAlquimiaRemittance(ReportRequestPayload requestPayload);

    DepositsResponsePayload getDeposits(ReportRequestPayload requestPayload);
}
