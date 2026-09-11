package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import com.kinorify.media_svc.media_svc.repository.MediaRepository;
import com.kinorify.media_svc.media_svc.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    @Override
    public Media createMedia(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    @Transactional(readOnly = true)
    public Media getMediaById(UUID mediaId) {
        return mediaRepository.findMediaById(mediaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Media not found: " + mediaId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByIds(List<UUID> mediaIds) {
        return mediaRepository.findMediaByIds(mediaIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByUploadedByProfileId(UUID profileId) {
        return mediaRepository.findMediaByUploadedByProfileId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByType(MediaType mediaType) {
        return mediaRepository.findMediaByType(mediaType.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByPurpose(MediaPurpose mediaPurpose) {
        return mediaRepository.findMediaByPurpose(mediaPurpose.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByStatus(MediaStatus status) {
        return mediaRepository.findMediaByStatus(status.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByTypeAndPurpose(MediaType mediaType, MediaPurpose mediaPurpose) {
        return mediaRepository.findMediaByTypeAndPurpose(
                mediaType.name(),
                mediaPurpose.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByProfileIdAndPurpose(UUID profileId, MediaPurpose mediaPurpose) {
        return mediaRepository.findMediaByProfileIdAndPurpose(
                profileId,
                mediaPurpose.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getMediaByProfileIdTypeAndPurpose(UUID profileId, MediaType mediaType, MediaPurpose mediaPurpose) {
        return mediaRepository.findMediaByProfileIdTypeAndPurpose(
                profileId,
                mediaType.name(),
                mediaPurpose.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getReadyMediaByIds(List<UUID> mediaIds) {
        return mediaRepository.findReadyMediaByIds(mediaIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> getReadyMediaByIdsAndPurpose(List<UUID> mediaIds, MediaPurpose mediaPurpose) {
        return mediaRepository.findReadyMediaByIdsAndPurpose(
                mediaIds,
                mediaPurpose.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsMediaById(UUID mediaId) {
        return mediaRepository.existsMediaById(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsMediaByIdAndPurpose(UUID mediaId, MediaPurpose mediaPurpose) {
        return mediaRepository.existsMediaByIdAndPurpose(
                mediaId,
                mediaPurpose.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsReadyMediaById(UUID mediaId) {
        return mediaRepository.existsReadyMediaById(mediaId);
    }

    @Override
    public Media updateMedia(Media media) {
        getMediaById(media.getMediaId());
        return mediaRepository.save(media);
    }

    @Override
    public Media updateStatus(UUID mediaId, MediaStatus status) {
        Media media = getMediaById(mediaId);
        media.setStatus(status);

        return mediaRepository.save(media);
    }

    @Override
    public Media markUploading(UUID mediaId) {
        Media media = getMediaById(mediaId);

        if (media.getStatus() != MediaStatus.CREATED) {
            throw new IllegalStateException(
                    "Only created media can begin uploading."
            );
        }

        media.setStatus(MediaStatus.UPLOADING);

        return mediaRepository.save(media);
    }

    @Override
    public Media markUploaded(UUID mediaId) {
        Media media = getMediaById(mediaId);

        if (media.getStatus() != MediaStatus.UPLOADING) {
            throw new IllegalStateException(
                    "Only uploading media can be marked uploaded."
            );
        }

        media.setStatus(MediaStatus.UPLOADED);
        media.setUploadedAt(OffsetDateTime.now());

        return mediaRepository.save(media);
    }

    @Override
    public Media markReady(UUID mediaId) {
        Media media = getMediaById(mediaId);

        if (media.getMediaType() == MediaType.VIDEO
                && media.getStatus() != MediaStatus.PROCESSING) {

            throw new IllegalStateException(
                    "Only processing video media can be marked ready."
            );
        }

        media.setStatus(MediaStatus.READY);
        media.setReadyAt(OffsetDateTime.now());

        return mediaRepository.save(media);
    }

    @Override
    public Media markFailed(UUID mediaId, String failureCode, String failureMessage) {
        Media media = getMediaById(mediaId);

        media.setStatus(MediaStatus.PROCESSING_FAILED);
        media.setFailureCode(failureCode);
        media.setFailureMessage(failureMessage);
        media.setFailedAt(OffsetDateTime.now());

        return mediaRepository.save(media);
    }

    @Override
    public Media markProcessing(UUID mediaId) {
        Media media = getMediaById(mediaId);

        if (media.getStatus() != MediaStatus.UPLOADED
                && media.getStatus() != MediaStatus.RETRYING) {

            throw new IllegalStateException(
                    "Only uploaded or retrying media can begin processing."
            );
        }

        media.setStatus(MediaStatus.PROCESSING);
        media.setProcessingStartedAt(OffsetDateTime.now());
        media.setFailureCode(null);
        media.setFailureMessage(null);
        media.setFailedAt(null);

        return mediaRepository.save(media);
    }

    @Override
    public Media markPlayable(UUID mediaId) {
        Media media = getMediaById(mediaId);

        if (media.getStatus() != MediaStatus.READY) {
            throw new IllegalStateException(
                    "Only ready media can be marked playable."
            );
        }

        media.setStatus(MediaStatus.PLAYABLE);
        media.setPlayableAt(OffsetDateTime.now());

        return mediaRepository.save(media);
    }

    @Override
    public Media markRetrying(UUID mediaId) {
        Media media = getMediaById(mediaId);

        media.setStatus(MediaStatus.RETRYING);
        media.setFailureCode(null);
        media.setFailureMessage(null);
        media.setFailedAt(null);

        return mediaRepository.save(media);
    }

    @Override
    public Media markTerminalFailed(UUID mediaId, String failureCode,
            String failureMessage) {

        Media media = getMediaById(mediaId);

        media.setStatus(MediaStatus.TERMINAL_FAILED);
        media.setFailureCode(failureCode);
        media.setFailureMessage(failureMessage);
        media.setFailedAt(OffsetDateTime.now());

        return mediaRepository.save(media);
    }
}
