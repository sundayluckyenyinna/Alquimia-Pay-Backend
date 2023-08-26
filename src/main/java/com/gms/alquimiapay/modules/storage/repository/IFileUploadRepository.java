package com.gms.alquimiapay.modules.storage.repository;

import com.gms.alquimiapay.modules.storage.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFileUploadRepository extends JpaRepository<FileUpload, Long>
{
    FileUpload findByUuid(String uuid);
    List<FileUpload> findAllByRemoteParentDirPath(String remoteDirPath);
    List<FileUpload> findAllByRemoteParentDirPathAndOwnerEmail(String remoteDirPath, String ownerEmail);
}
