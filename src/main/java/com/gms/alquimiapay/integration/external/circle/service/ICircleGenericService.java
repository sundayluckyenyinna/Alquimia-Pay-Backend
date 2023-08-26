package com.gms.alquimiapay.integration.external.circle.service;

import com.gms.alquimiapay.web.WebResponse;

import java.util.Map;

public interface ICircleGenericService
{
    String getCircleApiKey();
    String resolveCircleApiPath(String relativeUrl);

    Map<String, String> getCircleAuthHeader();

    WebResponse postExchangeWithCircle(String url, Object requestBody);
    WebResponse getExchangeWithCircle(String url);

    String getCircleDataJson(String webJson);
}
