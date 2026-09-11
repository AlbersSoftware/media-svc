package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.service.VideoProcessingService;
import com.kinorify.media_svc.media_svc.dto.MediaUploadResultDTO;
import com.kinorify.media_svc.media_svc.dto.S3ObjectMetadataDTO;
import com.kinorify.media_svc.media_svc.dto.response.ImageMediaResponseDTO;
import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.entity.MediaUploadSession;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import com.kinorify.media_svc.media_svc.enums.MediaUploadStatus;
import com.kinorify.media_svc.media_svc.service.MediaService;
import com.kinorify.media_svc.media_svc.service.MediaUploadService;
import com.kinorify.media_svc.media_svc.service.MediaUploadSessionService;
import com.kinorify.media_svc.media_svc.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaUploadServiceImpl implements MediaUploadService {

    private static final Duration PRESIGNED_UPLOAD_DURATION = Duration.ofMinutes(15);
    private static final Duration PRESIGNED_VIEW_DURATION = Duration.ofMinutes(15);
    private final VideoProcessingService videoProcessingService;
    private final MediaService mediaService;
    private final MediaUploadSessionService mediaUploadSessionService;
    private final S3StorageService s3StorageService;

    @Override
    public MediaUploadResultDTO createImageUpload(UUID profileId, MediaPurpose mediaPurpose,
            String originalFilename, String mimeType, Long expectedSizeBytes) {

        validateImageUpload(
                profileId,
                mediaPurpose,
                originalFilename,
                mimeType,
                expectedSizeBytes
        );

        Media media = Media.builder()
                .uploadedByProfileId(profileId)
                .mediaType(MediaType.IMAGE)
                .fileFormat(getFileExtension(originalFilename).toUpperCase(Locale.ROOT))
                .mediaPurpose(mediaPurpose)
                .status(MediaStatus.CREATED)
                .originalFilename(originalFilename)
                .displayName(originalFilename)
                .mimeType(mimeType)
                .sizeBytes(expectedSizeBytes)
                .build();

        media = mediaService.createMedia(media);

        String storageKey = buildImageStorageKey(
                media.getMediaId(),
                mediaPurpose,
                originalFilename
        );

        media.setOriginalStorageKey(storageKey);
        media = mediaService.updateMedia(media);

        OffsetDateTime expiresAt =
                OffsetDateTime.now().plus(PRESIGNED_UPLOAD_DURATION);

        MediaUploadSession uploadSession = MediaUploadSession.builder()
                .mediaId(media.getMediaId())
                .storageKey(storageKey)
                .status(MediaUploadStatus.CREATED)
                .expectedSizeBytes(expectedSizeBytes)
                .contentType(mimeType)
                .expiresAt(expiresAt)
                .build();

        uploadSession =
                mediaUploadSessionService.createUploadSession(uploadSession);

        String uploadUrl =
                s3StorageService.generatePresignedUploadUrl(
                        storageKey,
                        mimeType,
                        PRESIGNED_UPLOAD_DURATION
                );

        media = mediaService.markUploading(media.getMediaId());

        uploadSession =
                mediaUploadSessionService.markUploading(
                        uploadSession.getMediaUploadSessionId()
                );

        return new MediaUploadResultDTO(
                media,
                uploadSession,
                uploadUrl
        );
    }

    @Override
    public Media completeImageUpload(UUID mediaId, UUID uploadSessionId) {
        Media media = mediaService.getMediaById(mediaId);

        MediaUploadSession uploadSession =
                mediaUploadSessionService.getUploadSessionByIdAndMediaId(
                        uploadSessionId,
                        mediaId
                );

        validateImageUploadCompletion(
                media,
                uploadSession
        );

        S3ObjectMetadataDTO objectMetadata =
                s3StorageService.getObjectMetadata(
                        uploadSession.getStorageKey()
                );

        validateUploadedObject(
                media,
                uploadSession,
                objectMetadata
        );

        media.setSizeBytes(objectMetadata.sizeBytes());

        if (objectMetadata.contentType() != null) {
            media.setMimeType(objectMetadata.contentType());
        }

        mediaService.updateMedia(media);

        mediaService.markUploaded(mediaId);

        mediaUploadSessionService.markCompleted(uploadSessionId);

        return mediaService.markReady(mediaId);
    }

@Override
@Transactional(readOnly = true)
public ImageMediaResponseDTO getReadyImage(UUID mediaId, MediaPurpose mediaPurpose) {
    Media media = mediaService.getMediaById(mediaId);

    if (media.getMediaType() != MediaType.IMAGE) {
        throw new IllegalStateException(
                "Media is not an image: " + mediaId
        );
    }

    if (media.getMediaPurpose() != mediaPurpose) {
        throw new IllegalStateException(
                "Media purpose does not match requested resource."
        );
    }

    if (media.getStatus() != MediaStatus.READY) {
        throw new IllegalStateException(
                "Media is not ready: " + mediaId
        );
    }

    if (media.getOriginalStorageKey() == null
            || media.getOriginalStorageKey().isBlank()) {

        throw new IllegalStateException(
                "Media does not have an original storage key."
        );
    }

    return mapReadyImageResponse(media);
}

    private void validateImageUpload(UUID profileId, MediaPurpose mediaPurpose,
            String originalFilename, String mimeType, Long expectedSizeBytes) {

        if (profileId == null) {
            throw new IllegalArgumentException(
                    "Profile ID is required."
            );
        }

        if (mediaPurpose == null) {
            throw new IllegalArgumentException(
                    "Media purpose is required."
            );
        }

        if (mediaPurpose != MediaPurpose.COLLECTION_MEDIA
                && mediaPurpose != MediaPurpose.COLLECTION_THUMBNAIL
                && mediaPurpose != MediaPurpose.PROFILE_AVATAR) {

            throw new IllegalArgumentException(
                    "Unsupported image media purpose: " + mediaPurpose
            );
        }

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "Original filename is required."
            );
        }

        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Image MIME type is required."
            );
        }

        if (expectedSizeBytes == null || expectedSizeBytes <= 0) {
            throw new IllegalArgumentException(
                    "Expected file size must be greater than zero."
            );
        }
    }

    private void validateImageUploadCompletion(Media media, MediaUploadSession uploadSession) {
        if (media.getMediaType() != MediaType.IMAGE) {
            throw new IllegalStateException(
                    "Media is not an image: " + media.getMediaId()
            );
        }

        if (media.getStatus() != MediaStatus.UPLOADING) {
            throw new IllegalStateException(
                    "Media is not currently uploading: " + media.getMediaId()
            );
        }

        if (uploadSession.getStatus() != MediaUploadStatus.UPLOADING) {
            throw new IllegalStateException(
                    "Upload session is not currently uploading: "
                            + uploadSession.getMediaUploadSessionId()
            );
        }

        if (uploadSession.getExpiresAt() != null
                && uploadSession.getExpiresAt().isBefore(OffsetDateTime.now())) {

            mediaUploadSessionService.markExpired(
                    uploadSession.getMediaUploadSessionId()
            );

            throw new IllegalStateException(
                    "Upload session has expired: "
                            + uploadSession.getMediaUploadSessionId()
            );
        }

        if (!uploadSession.getStorageKey().equals(media.getOriginalStorageKey())) {
            throw new IllegalStateException(
                    "Upload session storage key does not match media storage key."
            );
        }
    }

    private void validateUploadedObject(Media media, MediaUploadSession uploadSession,
            S3ObjectMetadataDTO objectMetadata) {

        if (objectMetadata.sizeBytes() == null || objectMetadata.sizeBytes() <= 0) {
            throw new IllegalStateException(
                    "Uploaded S3 object is empty."
            );
        }

        if (!objectMetadata.sizeBytes().equals(uploadSession.getExpectedSizeBytes())) {
            throw new IllegalStateException(
                    "Uploaded file size does not match expected file size."
            );
        }

        if (objectMetadata.contentType() == null
                || !objectMetadata.contentType().startsWith("image/")) {

            throw new IllegalStateException(
                    "Uploaded S3 object does not have an image content type."
            );
        }

        if (uploadSession.getContentType() != null
                && !uploadSession.getContentType().equalsIgnoreCase(
                        objectMetadata.contentType()
                )) {

            throw new IllegalStateException(
                    "Uploaded content type does not match expected content type."
            );
        }

        if (!objectMetadata.storageKey().equals(media.getOriginalStorageKey())) {
            throw new IllegalStateException(
                    "Uploaded object storage key does not match media storage key."
            );
        }
    }

    private String buildImageStorageKey(UUID mediaId, MediaPurpose mediaPurpose,
            String originalFilename) {

        String extension = getFileExtension(originalFilename);

        return switch (mediaPurpose) {
            case COLLECTION_MEDIA ->
                    "originals/collection/media/"
                            + mediaId
                            + "/source."
                            + extension;

            case COLLECTION_THUMBNAIL ->
                    "originals/collection/thumbnails/"
                            + mediaId
                            + "/source."
                            + extension;

            case PROFILE_AVATAR ->
                    "originals/profile/avatars/"
                            + mediaId
                            + "/source."
                            + extension;
        };
    }

    private String getFileExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');

        if (index < 0 || index == originalFilename.length() - 1) {
            throw new IllegalArgumentException(
                    "File extension is required."
            );
        }

        return originalFilename
                .substring(index + 1)
                .toLowerCase(Locale.ROOT);
    }

@Override
@Transactional(readOnly = true)
public List<ImageMediaResponseDTO> getReadyImages(List<UUID> mediaIds, MediaPurpose mediaPurpose) {
    if (mediaIds == null || mediaIds.isEmpty()) {
        return List.of();
    }

    List<Media> media =
            mediaService.getReadyMediaByIdsAndPurpose(
                    mediaIds,
                    mediaPurpose
            );

    Map<UUID, Media> mediaById =
            media.stream()
                    .filter(item -> item.getMediaType() == MediaType.IMAGE)
                    .collect(
                            Collectors.toMap(
                                    Media::getMediaId,
                                    Function.identity()
                            )
                    );

    return mediaIds.stream()
            .distinct()
            .map(mediaById::get)
            .filter(item -> item != null)
            .map(this::mapReadyImageResponse)
            .toList();
}

private ImageMediaResponseDTO mapReadyImageResponse(Media media) {
    if (media.getOriginalStorageKey() == null
            || media.getOriginalStorageKey().isBlank()) {

        throw new IllegalStateException(
                "Media does not have an original storage key: "
                        + media.getMediaId()
        );
    }

    OffsetDateTime expiresAt =
            OffsetDateTime.now().plus(PRESIGNED_VIEW_DURATION);

    String imageUrl =
            s3StorageService.generatePresignedViewUrl(
                    media.getOriginalStorageKey(),
                    PRESIGNED_VIEW_DURATION
            );

    return ImageMediaResponseDTO.builder()
            .mediaId(media.getMediaId())
            .mediaType(media.getMediaType())
            .mediaPurpose(media.getMediaPurpose())
            .status(media.getStatus())
            .originalFilename(media.getOriginalFilename())
            .displayName(media.getDisplayName())
            .fileFormat(media.getFileFormat())
            .mimeType(media.getMimeType())
            .sizeBytes(media.getSizeBytes())
            .width(media.getWidth())
            .height(media.getHeight())
            .imageUrl(imageUrl)
            .imageUrlExpiresAt(expiresAt)
            .createdAt(media.getCreatedAt())
            .readyAt(media.getReadyAt())
            .build();
}

// video
@Override
public MediaUploadResultDTO createVideoUpload(UUID profileId,
        String originalFilename, String mimeType, Long expectedSizeBytes) {

    validateVideoUpload(
            profileId,
            originalFilename,
            mimeType,
            expectedSizeBytes
    );

    Media media = Media.builder()
            .uploadedByProfileId(profileId)
            .mediaType(MediaType.VIDEO)
            .fileFormat(getFileExtension(originalFilename).toUpperCase(Locale.ROOT))
            .mediaPurpose(MediaPurpose.COLLECTION_MEDIA)
            .status(MediaStatus.CREATED)
            .originalFilename(originalFilename)
            .displayName(originalFilename)
            .mimeType(mimeType)
            .sizeBytes(expectedSizeBytes)
            .processingGeneration(1)
            .build();

    media = mediaService.createMedia(media);

    String storageKey = buildVideoStorageKey(
            media.getMediaId(),
            originalFilename
    );

    media.setOriginalStorageKey(storageKey);
    media = mediaService.updateMedia(media);

    OffsetDateTime expiresAt =
            OffsetDateTime.now().plus(PRESIGNED_UPLOAD_DURATION);

    MediaUploadSession uploadSession = MediaUploadSession.builder()
            .mediaId(media.getMediaId())
            .storageKey(storageKey)
            .status(MediaUploadStatus.CREATED)
            .expectedSizeBytes(expectedSizeBytes)
            .contentType(mimeType)
            .expiresAt(expiresAt)
            .build();

    uploadSession =
            mediaUploadSessionService.createUploadSession(
                    uploadSession
            );

    String uploadUrl =
            s3StorageService.generatePresignedUploadUrl(
                    storageKey,
                    mimeType,
                    PRESIGNED_UPLOAD_DURATION
            );

    media = mediaService.markUploading(
            media.getMediaId()
    );

    uploadSession =
            mediaUploadSessionService.markUploading(
                    uploadSession.getMediaUploadSessionId()
            );

    return new MediaUploadResultDTO(
            media,
            uploadSession,
            uploadUrl
    );
}


@Override
public Media completeVideoUpload(UUID mediaId, UUID uploadSessionId) {

    Media media =
            mediaService.getMediaById(mediaId);

    MediaUploadSession uploadSession =
            mediaUploadSessionService.getUploadSessionByIdAndMediaId(
                    uploadSessionId,
                    mediaId
            );

    validateVideoUploadCompletion(
            media,
            uploadSession
    );

    S3ObjectMetadataDTO objectMetadata =
            s3StorageService.getObjectMetadata(
                    uploadSession.getStorageKey()
            );

    validateUploadedVideoObject(
            media,
            uploadSession,
            objectMetadata
    );

    media.setSizeBytes(
            objectMetadata.sizeBytes()
    );

    if (objectMetadata.contentType() != null) {
        media.setMimeType(
                objectMetadata.contentType()
        );
    }

    mediaService.updateMedia(media);

    mediaService.markUploaded(mediaId);

    mediaUploadSessionService.markCompleted(
            uploadSessionId
    );

    return videoProcessingService.startProcessing(
            mediaId
    );
}

private void validateVideoUpload(UUID profileId, String originalFilename,
        String mimeType, Long expectedSizeBytes) {

    if (profileId == null) {
        throw new IllegalArgumentException(
                "Profile ID is required."
        );
    }

    if (originalFilename == null
            || originalFilename.isBlank()) {

        throw new IllegalArgumentException(
                "Original filename is required."
        );
    }

    if (mimeType == null
            || !mimeType.startsWith("video/")) {

        throw new IllegalArgumentException(
                "Video MIME type is required."
        );
    }

    if (expectedSizeBytes == null
            || expectedSizeBytes <= 0) {

        throw new IllegalArgumentException(
                "Expected file size must be greater than zero."
        );
    }

    getFileExtension(originalFilename);
}


private void validateVideoUploadCompletion(Media media,
        MediaUploadSession uploadSession) {

    if (media.getMediaType() != MediaType.VIDEO) {
        throw new IllegalStateException(
                "Media is not a video: "
                        + media.getMediaId()
        );
    }

    if (media.getStatus() != MediaStatus.UPLOADING) {
        throw new IllegalStateException(
                "Media is not currently uploading: "
                        + media.getMediaId()
        );
    }

    if (uploadSession.getStatus()
            != MediaUploadStatus.UPLOADING) {

        throw new IllegalStateException(
                "Upload session is not currently uploading: "
                        + uploadSession.getMediaUploadSessionId()
        );
    }

    if (uploadSession.getExpiresAt() != null
            && uploadSession.getExpiresAt()
                    .isBefore(OffsetDateTime.now())) {

        mediaUploadSessionService.markExpired(
                uploadSession.getMediaUploadSessionId()
        );

        throw new IllegalStateException(
                "Upload session has expired: "
                        + uploadSession.getMediaUploadSessionId()
        );
    }

    if (!uploadSession.getStorageKey()
            .equals(media.getOriginalStorageKey())) {

        throw new IllegalStateException(
                "Upload session storage key does not match media storage key."
        );
    }
}


private void validateUploadedVideoObject(Media media,
        MediaUploadSession uploadSession,
        S3ObjectMetadataDTO objectMetadata) {

    if (objectMetadata.sizeBytes() == null
            || objectMetadata.sizeBytes() <= 0) {

        throw new IllegalStateException(
                "Uploaded S3 video object is empty."
        );
    }

    if (!objectMetadata.sizeBytes().equals(
            uploadSession.getExpectedSizeBytes())) {

        throw new IllegalStateException(
                "Uploaded video file size does not match expected file size."
        );
    }

    if (objectMetadata.contentType() == null
            || !objectMetadata.contentType()
                    .startsWith("video/")) {

        throw new IllegalStateException(
                "Uploaded S3 object does not have a video content type."
        );
    }

    if (uploadSession.getContentType() != null
            && !uploadSession.getContentType()
                    .equalsIgnoreCase(
                            objectMetadata.contentType()
                    )) {

        throw new IllegalStateException(
                "Uploaded video content type does not match expected content type."
        );
    }

    if (!objectMetadata.storageKey()
            .equals(media.getOriginalStorageKey())) {

        throw new IllegalStateException(
                "Uploaded object storage key does not match media storage key."
        );
    }
}


private String buildVideoStorageKey(UUID mediaId,
        String originalFilename) {

    String extension =
            getFileExtension(originalFilename);

    return "originals/collection/media/"
            + mediaId
            + "/source."
            + extension;
}


}
