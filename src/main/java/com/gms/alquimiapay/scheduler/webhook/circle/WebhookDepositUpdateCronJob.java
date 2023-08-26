package com.gms.alquimiapay.scheduler.webhook.circle;

import com.gms.alquimiapay.constants.CronExpression;
import com.gms.alquimiapay.constants.ModelStatus;
import com.gms.alquimiapay.modules.account.repository.IAccountDepositRepository;
import com.gms.alquimiapay.webhook.constants.CircleNotificationType;
import com.gms.alquimiapay.webhook.model.GmsWebhookModel;
import com.gms.alquimiapay.webhook.repository.IGmsWebhookModelRepository;
import com.gms.alquimiapay.webhook.service.circle.CirclePaymentNotificationService;
import com.gms.alquimiapay.webhook.service.circle.CircleWebhookFactory;
import com.google.gson.Gson;
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
public class WebhookDepositUpdateCronJob
{
    private final IGmsWebhookModelRepository webhookModelRepository;
    private final CircleWebhookFactory circleWebhookFactory;

    private static final Gson JSON = new Gson();

    @Autowired
    public WebhookDepositUpdateCronJob(
            IGmsWebhookModelRepository webhookModelRepository,
            IAccountDepositRepository accountDepositRepository,
            CirclePaymentNotificationService circlePaymentNotificationService, IAccountDepositRepository accountDepositRepository1, CircleWebhookFactory circleWebhookFactory) {
        this.webhookModelRepository = webhookModelRepository;
        this.circleWebhookFactory = circleWebhookFactory;
    }

    @Scheduled(cron = CronExpression.EVERY_5_SECONDS)
    public void processAccountDepositOrPayment(){

        // Fetch the notification object that have a notification type of Payment and has not been pending
        List<GmsWebhookModel> webhookModels = webhookModelRepository.findByNotificationTypeAndStatus(CircleNotificationType.PAYMENTS.name(), ModelStatus.PENDING.name());
        webhookModels.forEach(model -> {

            // Create the payment object.
            JSONObject jsonObject = new JSONObject(model.getWebhookDataJson());
            JSONObject paymentObject = jsonObject.getJSONObject("payment");

            model.setModelStatus(paymentObject.getString("status").toUpperCase());

            try {
                // Call the webhook service to process the webhook data.
                boolean done = circleWebhookFactory.doNotification(model.getNotificationType().toLowerCase(), paymentObject.toString());

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
