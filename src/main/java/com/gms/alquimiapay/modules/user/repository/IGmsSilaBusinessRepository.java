package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.GmsSilaBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGmsSilaBusinessRepository extends JpaRepository<GmsSilaBusiness, Long>
{
    GmsSilaBusiness findByBusinessEmail(String businessEmail);
}
