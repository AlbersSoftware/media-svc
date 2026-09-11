package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaProcessingJobStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaProcessingJobResponseDTO {

    private UUID mediaProcessingJobId;

    private UUID mediaId;

    private Integer processingGeneration;

    private String jobType;

    private MediaProcessingJobStatus status;

    private Short priority;

    private Integer attemptNumber;

    private Integer maxAttempts;

    private String failureMessage;

    private OffsetDateTime queuedAt;

    private OffsetDateTime startedAt;

    private OffsetDateTime completedAt;

    private OffsetDateTime failedAt;
}
