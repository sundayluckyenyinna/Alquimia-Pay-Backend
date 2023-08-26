package com.gms.alquimiapay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageProvider
{
    private final MessageSource messageSource;

    @Autowired
    public MessageProvider(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    public String getMessage(String code){
        return this.messageSource.getMessage("E".concat(code), null, Locale.ENGLISH);
    }
}
