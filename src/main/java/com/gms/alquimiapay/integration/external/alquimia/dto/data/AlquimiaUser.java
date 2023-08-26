package com.gms.alquimiapay.integration.external.alquimia.dto.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlquimiaUser
{
    private String login;
    private String password;
}
