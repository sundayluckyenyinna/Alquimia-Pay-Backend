package com.gms.alquimiapay.modules.report.payload.response;

import com.gms.alquimiapay.modules.report.dto.TransactionEntryDTO;
import com.gms.alquimiapay.modules.report.payload.data.ReportResponseData;
import lombok.Data;


@Data
public class TransactionEntriesResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private ReportResponseData<TransactionEntryDTO> responseData;
}
