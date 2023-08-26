package com.gms.alquimiapay.modules.transaction.payload.response;

import com.gms.alquimiapay.modules.transaction.model.CustomerBeneficiary;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerBeneficiaryListResponsePayload extends BaseResponse
{
    private List<CustomerBeneficiary> responseData;
}
