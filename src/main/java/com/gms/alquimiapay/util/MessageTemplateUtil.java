package com.gms.alquimiapay.util;

import com.gms.alquimiapay.constants.StringValues;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Provides HTMl templates as String
 */

@Slf4j
@Component
public class MessageTemplateUtil
{

    @Value("${gms.base-url}")
    private String baseUrl;

    public String getTemplateOf(@NonNull String fileName){
        String fullFileName = fileName.endsWith(".html") ? fileName : fileName.concat(".html");
        String fullPublicFilePath = baseUrl.concat(StringValues.FORWARD_SLASH).concat(fullFileName);
        log.info("Full template path: {}", fullPublicFilePath);

        Document document = null;
        try {
            document = Jsoup.connect(fullPublicFilePath).ignoreHttpErrors(true).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return document.outerHtml();
    }

}
