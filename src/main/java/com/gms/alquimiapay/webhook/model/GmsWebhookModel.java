package com.gms.alquimiapay.webhook.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_webhook_model")
@Getter
@Setter
public class GmsWebhookModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", columnDefinition = "text")
    private String messageId;

    @Column(name = "model_status")
    private String modelStatus;

    @Column(name = "internal_ref", columnDefinition = "text")
    private String internalRef;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "processing_status")
    private String status;

    @Column(name = "webhook_data_json", columnDefinition = "text")
    private String webhookDataJson;
}
