package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.GmsSilaBusinessOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IGmsSilaBusinessOfficerRepository extends JpaRepository<GmsSilaBusinessOfficer, Long>
{
    GmsSilaBusinessOfficer findByEmail(String email);
    List<GmsSilaBusinessOfficer> findByBusinessIdAndStatus(Long businessId, String status);
    List<GmsSilaBusinessOfficer> findByBusinessId(Long id);
    List<GmsSilaBusinessOfficer> findByEmailAndOfficerRoleAndStatus(String email, String role, String status);
}
