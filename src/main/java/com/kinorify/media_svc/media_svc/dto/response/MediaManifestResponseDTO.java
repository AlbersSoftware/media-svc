package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaManifestStatus;
import com.kinorify.media_svc.media_svc.enums.MediaManifestType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaManifestResponseDTO {

    private UUID mediaManifestId;

    private UUID mediaId;

    private Integer processingGeneration;

    private MediaManifestType manifestType;

    private MediaManifestStatus status;

    private Integer version;

    private String manifestUrl;

    private OffsetDateTime manifestUrlExpiresAt;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
