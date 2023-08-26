package com.gms.alquimiapay.payload;

import lombok.Data;

@Data
public class MailData
{
    private String recipientMails;
    private String subject;
    private String content;
    private String contentType;
}
