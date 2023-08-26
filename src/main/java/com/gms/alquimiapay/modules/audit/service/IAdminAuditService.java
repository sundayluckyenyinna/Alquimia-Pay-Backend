package com.gms.alquimiapay.modules.audit.service;

import com.gms.alquimiapay.modules.user.model.GmsAdmin;

public interface IAdminAuditService {

    void saveAudit(Object operationLogs, String code, String message, GmsAdmin admin);
}
