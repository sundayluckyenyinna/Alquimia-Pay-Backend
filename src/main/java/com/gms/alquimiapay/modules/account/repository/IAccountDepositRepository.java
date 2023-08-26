package com.gms.alquimiapay.modules.account.repository;

import com.gms.alquimiapay.modules.account.model.AccountDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IAccountDepositRepository extends JpaRepository<AccountDeposit, Long>
{
    AccountDeposit findByDepositId(String depositId);
    List<AccountDeposit> findBySourceCustomerEmail(String email);

    @Query(value = "SELECT * FROM gms_account_deposit WHERE source_type = 'WIRE' ORDER BY id DESC LIMIT 1", nativeQuery = true)
    AccountDeposit findLastRecord();

    @Query(value = "select * from gms_account_deposit where CAST (created_at as timestamp) >= :dateTime order by id asc", nativeQuery = true)
    List<AccountDeposit> findAllAccountDepositFromDate(@Param("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime);

    @Query(value = "select * from gms_account_deposit where CAST (created_at as timestamp) >= :start AND CAST (created_at as timestamp) <= :end  order by id asc", nativeQuery = true)
    List<AccountDeposit> findAllAccountDepositBetweenDate(@Param("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime, @Param("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime);
}
