package com.gms.alquimiapay.integration.external.sila.dto.identity.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaBusinessTypeResponseData;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaBusinessTypeResponseDTO extends GenericSilaResponseDTO
{
    @SerializedName("business_types")
    private List<SilaBusinessTypeResponseData> businessTypes;
}
