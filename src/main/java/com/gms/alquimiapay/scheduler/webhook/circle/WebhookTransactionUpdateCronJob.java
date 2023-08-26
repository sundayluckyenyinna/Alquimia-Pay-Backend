package com.gms.alquimiapay.scheduler.webhook.circle;

import com.gms.alquimiapay.constants.CronExpression;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.webhook.constants.CircleNotificationType;
import com.gms.alquimiapay.webhook.model.GmsWebhookModel;
import com.gms.alquimiapay.webhook.repository.IGmsWebhookModelRepository;
import com.gms.alquimiapay.webhook.service.circle.CircleTransferNotificationService;
import com.gms.alquimiapay.webhook.service.circle.CircleWebhookFactory;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class WebhookTransactionUpdateCronJob
{
    private final CircleWebhookFactory circleWebhookFactory;
    private final IGmsWebhookModelRepository webhookModelRepository;

    @Autowired
    public WebhookTransactionUpdateCronJob(
            CircleTransferNotificationService circleTransferNotificationService,
            CircleWebhookFactory circleWebhookFactory,
            IGmsWebhookModelRepository webhookModelRepository) {
        this.circleWebhookFactory = circleWebhookFactory;
        this.webhookModelRepository = webhookModelRepository;
    }

    @Scheduled(cron = CronExpression.EVERY_5_SECONDS)
    public void processCircleTransactionUpdate(){

        List<GmsWebhookModel> webhookModels = webhookModelRepository.findByNotificationTypeAndStatus(CircleNotificationType.TRANSFERS.name(), ModelStatus.PENDING.name());
        webhookModels.forEach(model -> {

            // Get the notification object
            JSONObject jsonObject = new JSONObject(model.getWebhookDataJson());
            JSONObject transferObject = jsonObject.getJSONObject("transfer");
            model.setModelStatus(transferObject.getString("status").toUpperCase());

            try {
                // Call the notification factory to process the notification object
                boolean done = circleWebhookFactory.doNotification(model.getNotificationType().toLowerCase(), transferObject.toString());

                if(done){
                    // Update the status of the webhook model to complete.
                    model.setStatus(ModelStatus.COMPLETE.name());
                    webhookModelRepository.saveAndFlush(model);
                }
            }catch (Exception e){
                model.setStatus(ModelStatus.FAILED.name());
                webhookModelRepository.saveAndFlush(model);
            }

        });
    }
}
