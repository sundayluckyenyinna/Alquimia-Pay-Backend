package com.gms.alquimiapay.integration.external.sila.dto.identity.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaDocumentType;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaDocumentTypeResponseDTO extends GenericSilaResponseDTO
{
    @SerializedName("document_types")
    private List<SilaDocumentType> documentTypes;
}
