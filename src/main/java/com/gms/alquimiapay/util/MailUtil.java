package com.gms.alquimiapay.util;

import com.gms.alquimiapay.constants.StringValues;
import com.gms.alquimiapay.payload.MailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.port}")
    private Integer mailPort;

    public String sendNotification(MailData mailData) throws MailException {
        HtmlEmail email = new HtmlEmail();
        email.setSmtpPort(mailPort);
        email.setAuthentication(mailUsername, mailPassword);
        email.setSSLOnConnect(true);
        try{
            email.setHostName(mailHost);
            List<String> recipients = getRecipientMails(mailData.getRecipientMails());
            for(String recipient : recipients){
                email.addTo(recipient.trim());
            }
            email.setFrom(mailUsername, "AlquimiaPay - Management");
            email.setSubject(mailData.getSubject());
            email.setHtmlMsg(mailData.getContent());
            return email.send();
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception occurred while trying to send email with attachment: {}", e.getMessage());
        }
        return null;
    }


    public String sendMailWithDataAndAttachment(MailData mailData, List<AttachmentData> attachmentDataList){
        HtmlEmail email = new HtmlEmail();
        email.setSmtpPort(mailPort);
        email.setAuthentication(mailUsername, mailPassword);
        email.setSSLOnConnect(true);
        try{
            email.setHostName(mailHost);
            List<String> recipients = getRecipientMails(mailData.getRecipientMails());
            for(String recipient : recipients){
                email.addTo(recipient.trim());
            }
            email.addTo(mailData.getRecipientMails());
            email.setFrom(mailUsername, "AlquimiaPay - Management");
            email.setSubject(mailData.getSubject());
            email.setHtmlMsg(mailData.getContent());
            for(AttachmentData a : attachmentDataList) {
                EmailAttachment attachment = new EmailAttachment();
                attachment.setPath(a.getAbsolutePath());
                attachment.setDescription(a.getDescription());
                attachment.setDisposition(EmailAttachment.ATTACHMENT);
                attachment.setName(a.getName());
                email.attach(attachment);
            }
            return email.send();
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception occurred while trying to send email with attachment");
        }
        return null;
    }

    private static List<String> getRecipientMails(String mails){
        return Arrays.stream(mails.split(StringValues.COMMA))
                .filter(token -> token != null && !token.isEmpty() && !token.isBlank())
                .map(String::trim)
                .collect(Collectors.toList());
    }

}
