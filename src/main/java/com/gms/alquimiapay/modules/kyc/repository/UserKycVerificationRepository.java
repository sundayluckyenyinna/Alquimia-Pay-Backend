package com.gms.alquimiapay.modules.kyc.repository;

import com.gms.alquimiapay.modules.kyc.model.UserKycVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKycVerificationRepository extends JpaRepository<UserKycVerification, Long>
{
    UserKycVerification findByUserEmail(String email);
}
