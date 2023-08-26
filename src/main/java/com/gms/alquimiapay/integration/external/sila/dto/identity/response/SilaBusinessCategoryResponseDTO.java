package com.gms.alquimiapay.integration.external.sila.dto.identity.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaBusinessCategoryResponseDTO extends GenericSilaResponseDTO
{
    @SerializedName("naics_categories")
    private Object businessSuperCategories;
}
