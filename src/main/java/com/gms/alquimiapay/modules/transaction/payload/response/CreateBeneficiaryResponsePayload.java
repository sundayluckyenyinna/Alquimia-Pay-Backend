package com.gms.alquimiapay.modules.transaction.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gms.alquimiapay.modules.transaction.model.CustomerBeneficiary;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateBeneficiaryResponsePayload extends BaseResponse
{
    private CustomerBeneficiary responseData;
}
