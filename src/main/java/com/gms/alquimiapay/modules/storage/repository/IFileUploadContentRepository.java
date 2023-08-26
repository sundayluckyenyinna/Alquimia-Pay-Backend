package com.gms.alquimiapay.modules.storage.repository;

import com.gms.alquimiapay.modules.storage.model.FileUploadContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFileUploadContentRepository extends JpaRepository<FileUploadContent, Long>
{
    FileUploadContent findByFileUploadId(String fileUploadId);
}
