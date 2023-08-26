package com.gms.alquimiapay.webhook.service;

import com.gms.alquimiapay.constants.Creator;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.modules.kyc.constant.Vendor;
import com.gms.alquimiapay.webhook.model.GmsWebhookModel;
import com.gms.alquimiapay.webhook.repository.IGmsWebhookModelRepository;
import com.google.gson.Gson;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class WebhookNotificationServiceHandler
{
    private final IGmsWebhookModelRepository webhookModelRepository;

    private final static Gson JSON = new Gson();

    @Autowired
    public WebhookNotificationServiceHandler(IGmsWebhookModelRepository webhookModelRepository) {
        this.webhookModelRepository = webhookModelRepository;
    }

    public String saveNotificationModelForUpdate(String notificationModelJson){

        try{
            // Properly format the plain text notification object to a proper JSON payload.
            JSONObject jsonObject = new JSONObject(notificationModelJson);

            // Save the notification to the database for further processing.
            String messageId = jsonObject.getString("MessageId");
            String messageJson = jsonObject.getString("Message");
            JSONObject messageObject = new JSONObject(messageJson);

            String notificationType = messageObject.getString("notificationType").toUpperCase();

            GmsWebhookModel webhookModel = new GmsWebhookModel();
            webhookModel.setInternalRef(UUID.randomUUID().toString());
            webhookModel.setCreatedAt(LocalDateTime.now().toString());
            webhookModel.setCreatedBy(Creator.SYSTEM.name());
            webhookModel.setUpdatedAt(LocalDateTime.now().toString());
            webhookModel.setUpdatedBy(Creator.SYSTEM.name());
            webhookModel.setVendor(Vendor.CIRCLE.name());
            webhookModel.setNotificationType(notificationType);
            webhookModel.setWebhookDataJson(messageObject.toString());
            webhookModel.setStatus(ModelStatus.PENDING.name());
            webhookModel.setMessageId(messageId);

            webhookModelRepository.saveAndFlush(webhookModel);
        }catch (Exception e){
            log.info("Exception while trying to listen to Circle webhook notification: {}", e.getMessage());
        }
        return "SUCCESS";
    }


}
