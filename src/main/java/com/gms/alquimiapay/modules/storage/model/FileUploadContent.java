package com.gms.alquimiapay.modules.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "gms_file_content")
@AllArgsConstructor
@RequiredArgsConstructor
public class FileUploadContent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", columnDefinition = "text")
    private String uuid;

    @Column(name = "file_upload_id", columnDefinition = "text")
    private String fileUploadId;

    @Column(name = "base_64_content", columnDefinition = "text")
    private String base64Content;
}
