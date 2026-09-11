package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteVideoUploadResponseDTO {

    private UUID mediaId;

    private MediaType mediaType;

    private MediaPurpose mediaPurpose;

    private MediaStatus status;

    private String originalFilename;

    private String displayName;

    private String fileFormat;

    private String mimeType;

    private Long sizeBytes;

    private Integer processingGeneration;

    private OffsetDateTime uploadedAt;

    private OffsetDateTime processingStartedAt;
}
