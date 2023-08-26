package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.GmsAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGmsAdminRepository extends JpaRepository<GmsAdmin, Long>
{
    GmsAdmin findByUsername(String username);
}
