package com.gms.alquimiapay.integration.external.sila.service;

import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaBusinessHeader;
import com.gms.alquimiapay.integration.external.sila.dto.auth.SilaIdentityHeader;
import com.gms.alquimiapay.web.WebResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ISilaGenericService {

    String getSilaAuthToken();

    SilaIdentityHeader getSilaIdentityHeader();

    SilaBusinessHeader getSilaBusinessHeader();

    WebResponse getExchangeWithSila(String url, Map<String, Object> params);
    WebResponse getExchangeWithSila(String url);
    WebResponse postExchangeWithSila(String url, Object requestObject, Map<String, Object> params);
    WebResponse postExchangeWithSila(String url, Object requestObject);
    WebResponse postFormExchangeWithSila(String url, Map<String, Object> formData);

    WebResponse postFormExchangeWithSilaRestTemplate(String url, Map<String, Object> formData);

    String resolveSilaUrl(String relativeUrl);
    String resolveSilaMessage(String internalMessage, String silaMessage);
}
