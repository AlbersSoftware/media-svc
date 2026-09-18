package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoProcessingStatusResponseDTO {

    private UUID mediaId;

    private Integer processingGeneration;

    private MediaStatus status;

    private String displayName;

    private List<MediaProcessingJobResponseDTO> jobs;

    private String failureCode;

    private String failureMessage;

    private OffsetDateTime processingStartedAt;

    private OffsetDateTime playableAt;

    private OffsetDateTime readyAt;

    private OffsetDateTime failedAt;
}
