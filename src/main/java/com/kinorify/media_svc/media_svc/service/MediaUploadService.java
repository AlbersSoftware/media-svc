package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.dto.MediaUploadResultDTO;
import com.kinorify.media_svc.media_svc.dto.response.ImageMediaResponseDTO;
import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;

import java.util.List;
import java.util.UUID;

public interface MediaUploadService {

    MediaUploadResultDTO createImageUpload(UUID profileId, MediaPurpose mediaPurpose,
            String originalFilename, String mimeType, Long expectedSizeBytes);

    Media completeImageUpload(UUID mediaId, UUID uploadSessionId);

    ImageMediaResponseDTO getReadyImage(UUID mediaId, MediaPurpose mediaPurpose);

    List<ImageMediaResponseDTO> getReadyImages(List<UUID> mediaIds, MediaPurpose mediaPurpose);


    MediaUploadResultDTO createVideoUpload(UUID profileId,
        String originalFilename, String mimeType, Long expectedSizeBytes);

    Media completeVideoUpload(UUID mediaId, UUID uploadSessionId);
}
