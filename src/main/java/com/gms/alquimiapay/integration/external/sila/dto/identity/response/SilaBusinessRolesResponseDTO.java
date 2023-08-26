package com.gms.alquimiapay.integration.external.sila.dto.identity.response;

import com.gms.alquimiapay.integration.external.sila.dto.GenericSilaResponseDTO;
import com.gms.alquimiapay.integration.external.sila.dto.identity.data.SilaBusinessRoleData;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SilaBusinessRolesResponseDTO extends GenericSilaResponseDTO
{
    @SerializedName("business_roles")
    private List<SilaBusinessRoleData> businessRoles;
}
