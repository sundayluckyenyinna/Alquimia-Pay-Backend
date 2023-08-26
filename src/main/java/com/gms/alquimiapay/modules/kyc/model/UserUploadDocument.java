package com.gms.alquimiapay.modules.kyc.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "gms_user_document")
@Getter
@Setter
public class UserUploadDocument
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "name")
    private String name;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "file_description")
    private String fileDescription;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "hash")
    private String hash;

    @Column(name = "file_content", columnDefinition = "text")
    private String fileContent;

    @Column(name = "identity_type")
    private String identityType;

    @Column(name = "file_binary", columnDefinition = "text")
    private String fileBinaryData;

    @Column(name = "absolute_path")
    private String absolutePath;
}
