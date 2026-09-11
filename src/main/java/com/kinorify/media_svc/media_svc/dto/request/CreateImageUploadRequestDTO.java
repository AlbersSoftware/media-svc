package com.kinorify.media_svc.media_svc.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateImageUploadRequestDTO {

    @NotBlank
    private String originalFilename;

    @NotBlank
    private String mimeType;

    @NotNull
    @Positive
    private Long sizeBytes;
}
