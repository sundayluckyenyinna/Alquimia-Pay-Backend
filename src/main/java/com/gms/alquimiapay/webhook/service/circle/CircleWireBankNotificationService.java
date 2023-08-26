package com.gms.alquimiapay.webhook.service.circle;

import org.springframework.stereotype.Service;


/**
 * This service handles the Circle notification webhook associated with Bank account creation, status update and follow-up.
 * Note that this service does not handle the deposit into the customer's (end-users) virtual account. It is handled by the Payment
 * Notification webhook instead.
 */
@Service
public class CircleWireBankNotificationService implements CircleNotification
{

    @Override
    public boolean doNotification(String notificationObject) {
        return true;
    }
}
