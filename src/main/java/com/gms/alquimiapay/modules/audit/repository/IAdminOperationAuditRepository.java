package com.gms.alquimiapay.modules.audit.repository;

import com.gms.alquimiapay.modules.audit.model.AdminOperationAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdminOperationAuditRepository extends JpaRepository<AdminOperationAudit, Long>
{

}
