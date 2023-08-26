package com.gms.alquimiapay.modules.audit.service;

import com.gms.alquimiapay.modules.audit.model.AdminOperationAudit;
import com.gms.alquimiapay.modules.audit.repository.IAdminOperationAuditRepository;
import com.gms.alquimiapay.modules.user.model.GmsAdmin;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AminAuditService implements IAdminAuditService
{
    private final IAdminOperationAuditRepository auditRepository;
    private static final Gson JSON = new Gson();

    @Override
    public void saveAudit(Object operationLog, String code, String message, GmsAdmin admin) {
        AdminOperationAudit audit = AdminOperationAudit.builder()
                .uuid(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now().toString())
                .adminUser(JSON.toJson(admin))
                .responseCode(code)
                .responseMessage(message)
                .operationLogs(JSON.toJson(operationLog))
                .build();
        auditRepository.saveAndFlush(audit);
    }
}
