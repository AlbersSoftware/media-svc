package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoMediaResponseDTO {

    private UUID mediaId;

    private MediaType mediaType;

    private MediaPurpose mediaPurpose;

    private MediaStatus status;

    private String originalFilename;

    private String displayName;

    private String fileFormat;

    private String mimeType;

    private Long sizeBytes;

    private Integer width;

    private Integer height;

    private Long durationMs;

    private String originalCodec;

    private Long originalBitrateBps;

    private Integer processingGeneration;

    private String failureCode;

    private String failureMessage;

    private List<MediaManifestResponseDTO> manifests;

    private List<MediaRenditionResponseDTO> renditions;

    private List<VideoThumbnailResponseDTO> thumbnails;

    private OffsetDateTime createdAt;

    private OffsetDateTime uploadedAt;

    private OffsetDateTime processingStartedAt;

    private OffsetDateTime playableAt;

    private OffsetDateTime readyAt;

    private OffsetDateTime failedAt;

    private OffsetDateTime updatedAt;
}
