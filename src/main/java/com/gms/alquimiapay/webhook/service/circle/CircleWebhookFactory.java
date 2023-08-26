package com.gms.alquimiapay.webhook.service.circle;

import com.gms.alquimiapay.webhook.constants.CircleNotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *  This is a factory method that routes the corresponding Circle Notification type to the appropriate Circle Notification Service.
 */

@Service
public class CircleWebhookFactory
{

    private final CirclePaymentNotificationService circlePaymentNotificationService;
    private final CircleTransferNotificationService circleTransferNotificationService;
    private final CircleWireBankNotificationService circleWireBankNotificationService;


    @Autowired
    public CircleWebhookFactory(
            CirclePaymentNotificationService circlePaymentNotificationService,
            CircleTransferNotificationService circleTransferNotificationService,
            CircleWireBankNotificationService circleWireBankNotificationService
    ) {
        this.circlePaymentNotificationService = circlePaymentNotificationService;
        this.circleTransferNotificationService = circleTransferNotificationService;
        this.circleWireBankNotificationService = circleWireBankNotificationService;
    }

    public boolean doNotification(String notificationType, String notificationObject){
         if(notificationType.equalsIgnoreCase(CircleNotificationType.PAYMENTS.type)){
             return circlePaymentNotificationService.doNotification(notificationObject);
         }
         else if(notificationType.equalsIgnoreCase(CircleNotificationType.TRANSFERS.type)){
             return circleTransferNotificationService.doNotification(notificationObject);
         }
         else if(notificationType.equalsIgnoreCase(CircleNotificationType.WIRE.type)){
            return circleWireBankNotificationService.doNotification(notificationObject);
         }
         return false;
     }
}
