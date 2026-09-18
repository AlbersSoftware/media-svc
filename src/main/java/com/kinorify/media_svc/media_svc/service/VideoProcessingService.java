package com.kinorify.media_svc.media_svc.service;
import com.kinorify.media_svc.media_svc.dto.VideoProcessingResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoRenditionsPeekDTO;
import com.kinorify.media_svc.media_svc.dto.response.VideoProcessingStatusResponseDTO;
import com.kinorify.media_svc.media_svc.entity.Media;

import java.util.UUID;

public interface VideoProcessingService {

    Media startProcessing(UUID mediaId);

    String getOriginalVideoDownloadUrl(UUID mediaId);

    String getDerivedStoragePrefix(UUID mediaId, Integer processingGeneration);

    Media completeProcessing(UUID mediaId, UUID processingJobId, VideoProcessingResultDTO result);

    VideoRenditionsPeekDTO peekRenditions(UUID mediaId);

    VideoProcessingStatusResponseDTO getProcessingStatus(UUID mediaId);
}
