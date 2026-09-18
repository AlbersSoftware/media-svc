package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.entity.VideoThumbnail;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import com.kinorify.media_svc.media_svc.enums.VideoThumbnailType;
import com.kinorify.media_svc.media_svc.repository.VideoThumbnailRepository;
import com.kinorify.media_svc.media_svc.service.MediaService;
import com.kinorify.media_svc.media_svc.service.S3StorageService;
import com.kinorify.media_svc.media_svc.service.VideoThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoThumbnailServiceImpl implements VideoThumbnailService {

    private final VideoThumbnailRepository videoThumbnailRepository;
    private final MediaService mediaService;
    private final S3StorageService s3StorageService;

    @Override
    @Transactional(readOnly = true)
    public void validateVideoMedia(UUID mediaId) {
        Media video = mediaService.getMediaById(mediaId);

        if (video.getMediaType() != MediaType.VIDEO) {
            throw new IllegalStateException(
                    "Media is not a video: " + mediaId
            );
        }
    }

    @Override
    public VideoThumbnail setVideoThumbnail(UUID mediaId, UUID thumbnailMediaId, VideoThumbnailType thumbnailType) {
        validateVideoMedia(mediaId);

        Media thumbnailMedia = mediaService.getMediaById(thumbnailMediaId);

        if (thumbnailMedia.getMediaType() != MediaType.IMAGE) {
            throw new IllegalStateException(
                    "Thumbnail media is not an image: " + thumbnailMediaId
            );
        }

        if (thumbnailMedia.getMediaPurpose() != MediaPurpose.VIDEO_THUMBNAIL) {
            throw new IllegalStateException(
                    "Image media is not a video thumbnail: " + thumbnailMediaId
            );
        }

        if (thumbnailMedia.getStatus() != MediaStatus.READY) {
            throw new IllegalStateException(
                    "Video thumbnail image is not ready: " + thumbnailMediaId
            );
        }

        if (thumbnailMedia.getOriginalStorageKey() == null
                || thumbnailMedia.getOriginalStorageKey().isBlank()) {
            throw new IllegalStateException(
                    "Video thumbnail image does not have a storage key."
            );
        }

        VideoThumbnail thumbnail =
                videoThumbnailRepository
                        .findLatestVideoThumbnailByMediaIdAndType(
                                mediaId,
                                thumbnailType.name()
                        )
                        .orElseGet(() ->
                                VideoThumbnail.builder()
                                        .mediaId(mediaId)
                                        .thumbnailType(thumbnailType)
                                        .build()
                        );

        UUID oldThumbnailMediaId = thumbnail.getThumbnailMediaId();
        String oldStorageKey = thumbnail.getStorageKey();

        thumbnail.setThumbnailMediaId(thumbnailMediaId);
        thumbnail.setWidth(thumbnailMedia.getWidth());
        thumbnail.setHeight(thumbnailMedia.getHeight());
        thumbnail.setMimeType(thumbnailMedia.getMimeType());
        thumbnail.setStorageKey(thumbnailMedia.getOriginalStorageKey());

        VideoThumbnail savedThumbnail =
                videoThumbnailRepository.save(thumbnail);

        if (oldThumbnailMediaId != null
                && !oldThumbnailMediaId.equals(thumbnailMediaId)) {

            mediaService.markDeleted(oldThumbnailMediaId);

            if (oldStorageKey != null && !oldStorageKey.isBlank()) {
                deleteOldThumbnailAfterCommit(oldStorageKey);
            }
        }

        return savedThumbnail;
    }

    @Override
    @Transactional(readOnly = true)
    public VideoThumbnail getVideoThumbnailById(UUID thumbnailId) {
        return videoThumbnailRepository.findVideoThumbnailById(thumbnailId)
                .orElseThrow(() -> new IllegalStateException(
                        "Video thumbnail not found: " + thumbnailId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoThumbnail> getVideoThumbnailsByMediaId(UUID mediaId) {
        return videoThumbnailRepository.findVideoThumbnailsByMediaId(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoThumbnail getVideoThumbnailByMediaIdAndType(UUID mediaId, VideoThumbnailType thumbnailType) {
        return videoThumbnailRepository
                .findLatestVideoThumbnailByMediaIdAndType(
                        mediaId,
                        thumbnailType.name()
                )
                .orElseThrow(() -> new IllegalStateException(
                        "Video thumbnail not found for media: "
                                + mediaId
                                + ", type: "
                                + thumbnailType
                ));
    }

    private void deleteOldThumbnailAfterCommit(String storageKey) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        s3StorageService.deleteObject(storageKey);
                    }
                }
        );
    }
}
