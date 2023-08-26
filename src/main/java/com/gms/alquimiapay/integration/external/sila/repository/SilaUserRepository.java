package com.gms.alquimiapay.integration.external.sila.repository;

import com.gms.alquimiapay.integration.external.sila.model.SilaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SilaUserRepository extends JpaRepository<SilaUser, Long>
{
    SilaUser findByGmsUserId(String userId);
    SilaUser findByGmsUserEmail(String userEmail);
    SilaUser findBySilaUserHandle(String userHandle);
}
