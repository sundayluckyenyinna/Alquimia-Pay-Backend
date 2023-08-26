package com.gms.alquimiapay.modules.report.payload.response;

import com.gms.alquimiapay.modules.report.dto.AlquimiaRemittanceDTO;
import com.gms.alquimiapay.modules.report.payload.data.ReportResponseData;
import lombok.Data;


@Data
public class AlquimiaRemittanceResponsePayload
{
    private String responseCode;
    private String responseMessage;
    private ReportResponseData<AlquimiaRemittanceDTO> responseData;
}
