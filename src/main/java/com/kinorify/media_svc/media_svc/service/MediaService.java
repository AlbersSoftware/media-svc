package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;

import java.util.List;
import java.util.UUID;

public interface MediaService {

    Media createMedia(Media media);

    Media getMediaById(UUID mediaId);

    List<Media> getMediaByIds(List<UUID> mediaIds);

    List<Media> getMediaByUploadedByProfileId(UUID profileId);

    List<Media> getMediaByType(MediaType mediaType);

    List<Media> getMediaByPurpose(MediaPurpose mediaPurpose);

    List<Media> getMediaByStatus(MediaStatus status);

    List<Media> getMediaByTypeAndPurpose(MediaType mediaType, MediaPurpose mediaPurpose);

    List<Media> getMediaByProfileIdAndPurpose(UUID profileId, MediaPurpose mediaPurpose);

    List<Media> getMediaByProfileIdTypeAndPurpose(UUID profileId, MediaType mediaType, MediaPurpose mediaPurpose);

    List<Media> getReadyMediaByIds(List<UUID> mediaIds);

    List<Media> getReadyMediaByIdsAndPurpose(List<UUID> mediaIds, MediaPurpose mediaPurpose);

    boolean existsMediaById(UUID mediaId);

    boolean existsMediaByIdAndPurpose(UUID mediaId, MediaPurpose mediaPurpose);

    boolean existsReadyMediaById(UUID mediaId);

    Media updateMedia(Media media);

    Media updateStatus(UUID mediaId, MediaStatus status);

    Media markUploading(UUID mediaId);

    Media markUploaded(UUID mediaId);

    Media markReady(UUID mediaId);

    Media markFailed(UUID mediaId, String failureCode, String failureMessage);

    Media markProcessing(UUID mediaId);

    Media markPlayable(UUID mediaId);

    Media markRetrying(UUID mediaId);

    Media markTerminalFailed(UUID mediaId, String failureCode, String failureMessage);

    Media markNoRenditionsNeeded(UUID mediaId);

    Media markSqsFailed(UUID mediaId, String failureCode, String failureMessage);

    Media markRenditionsFailed(UUID mediaId, String failureCode, String failureMessage);

    Media markMediaCallbackFailed(UUID mediaId, String failureCode, String failureMessage);

    Media markS3UploadFailed(UUID mediaId, String failureCode, String failureMessage);

    Media markDeleted(UUID mediaId);
}
