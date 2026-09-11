package com.kinorify.media_svc.media_svc.dto;

import com.kinorify.media_svc.media_svc.enums.MediaStatus;

import java.util.List;
import java.util.UUID;

public record VideoRenditionsPeekDTO(
        UUID mediaId,
        Integer processingGeneration,
        String derivedStoragePrefix,
        int objectCount,
        List<String> objectKeys,
        boolean hlsManifestPresent,
        boolean dashManifestPresent,
        boolean databaseOutputsReady,
        boolean statusUpdated,
        MediaStatus mediaStatus
) {
}
