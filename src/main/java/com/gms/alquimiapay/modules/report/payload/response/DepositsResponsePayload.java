package com.gms.alquimiapay.modules.report.payload.response;

import com.gms.alquimiapay.modules.report.dto.AccountDepositDTO;
import com.gms.alquimiapay.modules.report.payload.data.ReportResponseData;
import lombok.Data;


@Data
public class DepositsResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private ReportResponseData<AccountDepositDTO> responseData;
}
