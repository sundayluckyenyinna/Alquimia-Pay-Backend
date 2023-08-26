package com.gms.alquimiapay.modules.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "gms_admin_audit")
@RequiredArgsConstructor
@AllArgsConstructor
public class AdminOperationAudit
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "operation_logs", columnDefinition = "text")
    private String operationLogs;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "response_message", columnDefinition = "text")
    private String responseMessage;

    @Column(name = "created_at", columnDefinition = "text")
    private String createdAt;

    @Column(name = "admin_user", columnDefinition = "text")
    private String adminUser;
}
