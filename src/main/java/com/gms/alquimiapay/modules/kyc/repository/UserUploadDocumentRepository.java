package com.gms.alquimiapay.modules.kyc.repository;

import com.gms.alquimiapay.modules.kyc.model.UserUploadDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserUploadDocumentRepository extends JpaRepository<UserUploadDocument, Long>
{
    List<UserUploadDocument> findByUserEmail(String email);
    UserUploadDocument findByUserEmailAndDocumentType(String email, String docType);
}
