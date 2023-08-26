package com.gms.alquimiapay.integration.external.alquimia.dto.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalDetails
{
    private String calle;
    private String noExt;
    private String noInt;
    private String colonia;
    private String municipio;
    private String codPostal;
    private String tel;
    private String ladaTel;
    private String ciudad;
    private String estado;
    private String pais;
}
