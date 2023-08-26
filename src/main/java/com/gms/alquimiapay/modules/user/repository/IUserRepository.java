package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.GmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<GmsUser, Long>
{
    GmsUser findByEmailAddress(String emailAddress);
    GmsUser findByUsername(String username);
    GmsUser findByMobileNumber(String phoneNumber);

    @Query(value = "SELECT * FROM gms_users where CAST(created_at as timestamp) >= :dateTime ORDER BY id ASC", nativeQuery = true)
    List<GmsUser> findAllUsersFromDateTime(@Param ("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime);

    @Query(value = "SELECT * FROM gms_users where CAST(created_at as timestamp) >= :start AND CAST(created_at as timestamp) <= :end ORDER BY id ASC", nativeQuery = true)
    List<GmsUser> findAllUsersBetweenDateTimes(@Param("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDatetime, @Param("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime);
}
