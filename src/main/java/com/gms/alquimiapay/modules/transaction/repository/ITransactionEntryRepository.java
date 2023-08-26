package com.gms.alquimiapay.modules.transaction.repository;

import com.gms.alquimiapay.modules.transaction.model.TransactionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ITransactionEntryRepository extends JpaRepository<TransactionEntry, Long> {

    TransactionEntry findByFeeReference(String feeReference);
    TransactionEntry findByExternalRef(String externalRef);
    TransactionEntry findByInternalRef(String internalRef);
    List<TransactionEntry> findByCustomerEmail(String customerEmail);

    List<TransactionEntry> findByExternalStatus(String status);

    @Query(value = "select * from gms_transaction_entry where CAST(created_at as timestamp) >= :dateTime order by id asc", nativeQuery = true)
    List<TransactionEntry> findAllTransactionEntryFromDate(@Param("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime dateTime);

    @Query(value = "select * from gms_transaction_entry where CAST(created_at as timestamp) >= :start AND CAST(created_at as timestamp) <= :end order by id asc", nativeQuery = true)
    List<TransactionEntry> findAllTransactionEntriesBetweenDate(@Param("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime, @Param("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime);
}
