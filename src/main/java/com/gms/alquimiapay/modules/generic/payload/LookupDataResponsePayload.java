package com.gms.alquimiapay.modules.generic.payload;

import com.gms.alquimiapay.model.LookupData;
import com.gms.alquimiapay.payload.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class LookupDataResponsePayload extends BaseResponse
{
    private List<LookupData> responseData;
}
