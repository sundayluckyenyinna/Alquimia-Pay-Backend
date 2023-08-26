package com.gms.alquimiapay.modules.storage.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "gms_file_upload")
@AllArgsConstructor
@RequiredArgsConstructor
public class FileUpload
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", columnDefinition = "text")
    private String uuid;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_extension")
    private String extension;

    @Column(name = "file_name_ext")
    private String filenameWithExtension;

    @Column(name = "unique_file_name_ext")
    private String uniqueFileNameWithExtension;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "is_remote_saved")
    private boolean isRemotelySaved;

    @Column(name = "remote_parent_dir_path")
    private String remoteParentDirPath;

    @Column(name = "public_link")
    private String publicLink;

    @Column(name = "owner_email")
    private String ownerEmail;

    @Column(name = "created_at", columnDefinition = "text")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

}
