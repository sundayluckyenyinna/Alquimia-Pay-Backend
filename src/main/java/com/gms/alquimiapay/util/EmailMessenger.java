package com.gms.alquimiapay.util;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.payload.MailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailMessenger
{

    private final MailUtil mailUtil;
    private final MessageTemplateUtil messageTemplateUtil;


    public void sendMessage(String recipient, String templateFileName, String subject){
        MailData mailData = buildMailData(recipient, templateFileName, subject);
        mailUtil.sendNotification(mailData);
    }

    public void sendMessageWithData(String recipient, String fileName, String subject, Map<String, String> data){
        MailData mailData = buildMailDataWithData(recipient, fileName, subject, data);
        mailUtil.sendNotification(mailData);
    }
    private MailData buildMailData(String recipient, String fileName, String subject){
        MailData mailData = new MailData();
        String outerDocumentHtml= messageTemplateUtil.getTemplateOf(fileName);
        mailData.setContent(outerDocumentHtml);
        mailData.setRecipientMail(recipient);
        mailData.setSubject(subject);

        return mailData;
    }

    private MailData buildMailDataWithData(String recipient, String fileName, String subject, Map<String, String> data){
        MailData mailData = new MailData();
        String outerDocumentHtml= messageTemplateUtil.getTemplateOf(fileName);
        for(Map.Entry<String, String> entry: data.entrySet()){
            String replaceableKey = StringValues.OPENING_BRACE.concat(entry.getKey()).concat(StringValues.CLOSING_BRACE);
            outerDocumentHtml = outerDocumentHtml.replace(replaceableKey, entry.getValue());
        }
        mailData.setContent(outerDocumentHtml);
        mailData.setRecipientMail(recipient);
        mailData.setSubject(subject);

        return mailData;
    }

    public void sendMailWithDataAndAttachment(String recipient, String fileName, String subject, Map<String, String> data, List<AttachmentData> attachmentDataList){
        MailData mailData = buildMailDataWithData(recipient, fileName, subject, data);
        String emailSendMessage = mailUtil.sendMailWithDataAndAttachment(mailData, attachmentDataList);
        log.info("Email sending final message: {}", emailSendMessage);
    }
}
