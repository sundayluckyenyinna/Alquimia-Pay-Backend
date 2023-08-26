package com.gms.alquimiapay.modules.report.payload.response;

import com.gms.alquimiapay.modules.report.dto.UserDTO;
import com.gms.alquimiapay.modules.report.payload.data.ReportResponseData;
import lombok.Data;


@Data
public class UsersResponsePayload
{
    private String responseCode;

    private String responseMessage;

    private ReportResponseData<UserDTO> responseData;
}
