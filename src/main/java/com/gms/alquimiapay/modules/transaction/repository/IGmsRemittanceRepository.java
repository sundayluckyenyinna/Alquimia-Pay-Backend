package com.gms.alquimiapay.modules.transaction.repository;

import com.gms.alquimiapay.modules.transaction.model.GmsRemittance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IGmsRemittanceRepository extends JpaRepository<GmsRemittance, Long>
{
    @Query(value = "select * from gms_remittance where CAST(created_at as timestamp) >= :start AND CAST(created_at as timestamp) <= :end order by id asc", nativeQuery = true)
    List<GmsRemittance> findAllRemittanceBetweenDate(@Param("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime, @Param("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime);
}
