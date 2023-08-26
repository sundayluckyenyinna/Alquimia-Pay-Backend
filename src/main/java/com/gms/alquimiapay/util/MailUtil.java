package com.gms.alquimiapay.util;

import com.gms.alquimiapay.payload.MailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil{

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.port}")
    private Integer mailPort;

    private final JavaMailSender mailSender;

    public void sendNotification(MailData mailData) throws MailException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "utf-8");
        try {
            messageHelper.setText(mailData.getContent(), true);
            messageHelper.setTo(mailData.getRecipientMail());
            messageHelper.setFrom(mailUsername);
            messageHelper.setSubject(mailData.getSubject());
            mailSender.send(message);
        } catch (MessagingException exception) {
            exception.printStackTrace();
            log.info("Exception while sending message: {}", exception.getMessage());
        }
    }

    public String sendMailWithDataAndAttachment(MailData mailData, List<AttachmentData> attachmentDataList){
        HtmlEmail email = new HtmlEmail();
        email.setSmtpPort(mailPort);
        email.setAuthentication(mailUsername, mailPassword);
        email.setSSLOnConnect(true);
        try{
            email.setHostName(mailHost);
            email.addTo(mailData.getRecipientMail());
            email.setFrom(mailUsername, "GMS - Management");
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
}
