package com.kinorify.media_svc.media_svc.dto;

import java.util.List;

public record VideoProcessingResultDTO(
        boolean successful,
        String failureMessage,
        List<VideoRenditionResultDTO> renditions,
        List<VideoManifestResultDTO> manifests
) {
}
