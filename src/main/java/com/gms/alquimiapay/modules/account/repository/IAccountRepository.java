package com.gms.alquimiapay.modules.account.repository;

import com.gms.alquimiapay.modules.account.model.VirtualAccountCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAccountRepository extends JpaRepository<VirtualAccountCache, Long>
{
    VirtualAccountCache findByInternalCustomerEmail(String email);
    List<VirtualAccountCache> findByStatus(String status);
    VirtualAccountCache findByExternalId(String externalId);
}
