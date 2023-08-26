package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.GmsUserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserDeviceRepository extends JpaRepository<GmsUserDevice, Long>
{
    GmsUserDevice findByDeviceIdAndOwnerEmail(String deviceId, String ownerEmail);
    List<GmsUserDevice> findByOwnerEmail(String email);
}
