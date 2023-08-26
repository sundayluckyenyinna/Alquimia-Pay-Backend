package com.gms.alquimiapay.modules.cheque.repository;

import com.gms.alquimiapay.modules.cheque.model.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IChequeRepository extends JpaRepository<Cheque, Long>
{
    Cheque findByUuid(String uuid);
    Cheque findByFileUUID(String fileUuid);
}
