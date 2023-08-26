package com.gms.alquimiapay.webhook.repository;

import com.gms.alquimiapay.webhook.model.GmsWebhookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IGmsWebhookModelRepository extends JpaRepository<GmsWebhookModel, Long>
{
    List<GmsWebhookModel> findByNotificationTypeAndStatus(String notificationType, String status);
}
