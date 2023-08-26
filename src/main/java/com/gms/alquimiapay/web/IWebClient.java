package com.gms.alquimiapay.web;

import java.util.Map;

public interface IWebClient
{
    String getForObject(String url, Map<String, String> headers, Map<String, Object> params);
    String getForObject(String url, Map<String, String> headers);
    String getForObject(String url);
    String postForObject(String url, String requestJson, Map<String, String> headers, Map<String, Object> params);
    String postForObject(String url, Object requestObject, Map<String, String> headers, Map<String, Object> params);
    String postForObject(String url, Object requestObject);

    String postForForm(String url, Map<String, Object> formData, Map<String, String> headers);

    String postForFormRestTemplate(String url, Map<String, Object> formData, Map<String, String> headers);
}
