package com.gms.alquimiapay.modules.user.repository;

import com.gms.alquimiapay.modules.user.model.GmsUserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserOtpRepository extends JpaRepository<GmsUserOtp, Long>
{
    GmsUserOtp findByOtpTypeAndOtpOwner(String type, String owner);
}
