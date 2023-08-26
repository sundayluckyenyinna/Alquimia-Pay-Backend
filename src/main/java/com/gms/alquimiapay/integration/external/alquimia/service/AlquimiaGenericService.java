package com.gms.alquimiapay.integration.external.alquimia.service;

import com.gms.alquimiapay.integration.external.alquimia.dto.data.AlquimiaUser;
import com.gms.alquimiapay.web.WebResponse;

public interface AlquimiaGenericService {

    AlquimiaUser getAlquimiaLoginUser();
    WebResponse postExchangeWithAlquimia(String url, Object requestBody);
}
