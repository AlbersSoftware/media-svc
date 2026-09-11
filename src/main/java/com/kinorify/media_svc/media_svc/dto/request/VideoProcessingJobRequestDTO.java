package com.kinorify.media_svc.media_svc.dto.request;

import java.util.UUID;

public record VideoProcessingJobRequestDTO(
        UUID mediaId,
        UUID processingJobId,
        Integer processingGeneration,
        String sourceBucket,
        String sourceStorageKey,
        String derivedBucket,
        String derivedStoragePrefix
) {
}
