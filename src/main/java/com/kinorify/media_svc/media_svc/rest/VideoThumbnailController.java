package com.kinorify.media_svc.media_svc.controller;

import com.kinorify.media_svc.media_svc.dto.MediaUploadResultDTO;
import com.kinorify.media_svc.media_svc.dto.request.CreateImageUploadRequestDTO;
import com.kinorify.media_svc.media_svc.dto.response.CreateImageUploadResponseDTO;
import com.kinorify.media_svc.media_svc.dto.response.ImageMediaResponseDTO;
import com.kinorify.media_svc.media_svc.entity.VideoThumbnail;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.VideoThumbnailType;
import com.kinorify.media_svc.media_svc.service.MediaUploadService;
import com.kinorify.media_svc.media_svc.service.VideoThumbnailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media/video-thumbnails")
public class VideoThumbnailController {

    private final MediaUploadService mediaUploadService;
    private final VideoThumbnailService videoThumbnailService;

    @PostMapping("/{mediaId}/uploads")
    public ResponseEntity<CreateImageUploadResponseDTO> createVideoThumbnailUpload(
            @PathVariable UUID mediaId,
            @RequestParam UUID profileId,
            @Valid @RequestBody CreateImageUploadRequestDTO request) {

        videoThumbnailService.validateVideoMedia(mediaId);

        MediaUploadResultDTO result =
                mediaUploadService.createImageUpload(
                        profileId,
                        MediaPurpose.VIDEO_THUMBNAIL,
                        request.getOriginalFilename(),
                        request.getMimeType(),
                        request.getSizeBytes()
                );

        CreateImageUploadResponseDTO response =
                CreateImageUploadResponseDTO.builder()
                        .mediaId(result.media().getMediaId())
                        .uploadSessionId(result.uploadSession().getMediaUploadSessionId())
                        .uploadUrl(result.uploadUrl())
                        .contentType(result.uploadSession().getContentType())
                        .mediaStatus(result.media().getStatus())
                        .uploadStatus(result.uploadSession().getStatus())
                        .expiresAt(result.uploadSession().getExpiresAt())
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{mediaId}/uploads/{thumbnailMediaId}/{uploadSessionId}/complete")
    public ResponseEntity<VideoThumbnail> completeVideoThumbnailUpload(
            @PathVariable UUID mediaId,
            @PathVariable UUID thumbnailMediaId,
            @PathVariable UUID uploadSessionId) {

        mediaUploadService.completeImageUpload(
                thumbnailMediaId,
                uploadSessionId
        );

        VideoThumbnail thumbnail =
                videoThumbnailService.setVideoThumbnail(
                        mediaId,
                        thumbnailMediaId,
                        VideoThumbnailType.THUMBNAIL
                );

        return ResponseEntity.ok(thumbnail);
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<ImageMediaResponseDTO> getVideoThumbnail(@PathVariable UUID mediaId) {
        try {
            VideoThumbnail thumbnail =
                    videoThumbnailService.getVideoThumbnailByMediaIdAndType(
                            mediaId,
                            VideoThumbnailType.THUMBNAIL
                    );

            ImageMediaResponseDTO response =
                    mediaUploadService.getReadyImage(
                            thumbnail.getThumbnailMediaId(),
                            MediaPurpose.VIDEO_THUMBNAIL
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
