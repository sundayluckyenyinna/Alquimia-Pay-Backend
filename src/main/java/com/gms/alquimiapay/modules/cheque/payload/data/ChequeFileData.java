package com.gms.alquimiapay.modules.cheque.payload.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChequeFileData
{
    @NotNull(message = "base64Content cannot be null")
    @NotEmpty(message = "base64Content cannot be empty")
    @NotBlank(message = "base64Content cannot be blank")
    @ApiModelProperty(example = "iVRFJKWOL/ryukmdseoid_d983dndh...")
    private String base64Content;

    @NotNull(message = "fileType cannot be null")
    @NotEmpty(message = "fileType cannot be empty")
    @NotBlank(message = "fileType cannot be blank")
    @ApiModelProperty(example = "IMAGE | PDF | DOC | AUDIO | VIDEO | STREAM")
    private String fileType;

    @NotNull(message = "fileName cannot be null")
    @NotEmpty(message = "fileName cannot be empty")
    @NotBlank(message = "fileName cannot be blank")
    @ApiModelProperty(example = "check")
    private String fileName;

    @NotNull(message = "extension cannot be null")
    @NotEmpty(message = "extension cannot be empty")
    @NotBlank(message = "extension cannot be blank")
    @ApiModelProperty(example = "PNG | JPG | PDF")
    private String extension;

    @NotNull(message = "mimeType cannot be null")
    @NotEmpty(message = "mimeType cannot be empty")
    @NotBlank(message = "mimeType cannot be blank")
    @ApiModelProperty(example = "image/png")
    private String mimeType;

}
