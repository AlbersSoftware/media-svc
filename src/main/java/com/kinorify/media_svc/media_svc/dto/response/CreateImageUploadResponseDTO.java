package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaUploadStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateImageUploadResponseDTO {

    private UUID mediaId;

    private UUID uploadSessionId;

    private String uploadUrl;

    private String contentType;

    private MediaStatus mediaStatus;

    private MediaUploadStatus uploadStatus;

    private OffsetDateTime expiresAt;
}
