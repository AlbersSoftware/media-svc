package com.kinorify.media_svc.media_svc.rest;

import com.kinorify.media_svc.media_svc.dto.MediaUploadResultDTO;
import com.kinorify.media_svc.media_svc.dto.request.CreateImageUploadRequestDTO;
import com.kinorify.media_svc.media_svc.dto.response.CompleteImageUploadResponseDTO;
import com.kinorify.media_svc.media_svc.dto.response.CreateImageUploadResponseDTO;
import com.kinorify.media_svc.media_svc.dto.response.ImageMediaResponseDTO;
import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.service.MediaUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kinorify.media_svc.media_svc.dto.request.ResolveImageMediaRequestDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media/collection-media/images")
@RequiredArgsConstructor
public class CollectionMediaImageController {

    private final MediaUploadService mediaUploadService;

    @PostMapping("/uploads")
    public ResponseEntity<CreateImageUploadResponseDTO> createUpload(@RequestParam UUID profileId,
            @Valid @RequestBody CreateImageUploadRequestDTO request) {

        MediaUploadResultDTO result =
                mediaUploadService.createImageUpload(
                        profileId,
                        MediaPurpose.COLLECTION_MEDIA,
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

    @PostMapping("/{mediaId}/uploads/{uploadSessionId}/complete")
    public ResponseEntity<CompleteImageUploadResponseDTO> completeUpload(@PathVariable UUID mediaId,
            @PathVariable UUID uploadSessionId) {

        Media media =
                mediaUploadService.completeImageUpload(
                        mediaId,
                        uploadSessionId
                );

        return ResponseEntity.ok(
                mapCompleteResponse(media)
        );
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<ImageMediaResponseDTO> getImage(@PathVariable UUID mediaId) {
        return ResponseEntity.ok(
                mediaUploadService.getReadyImage(
                        mediaId,
                        MediaPurpose.COLLECTION_MEDIA
                )
        );
    }
// acts as a batch for getting media ids for gallery
@PostMapping("/resolve")
public ResponseEntity<List<ImageMediaResponseDTO>> resolveImages(
        @Valid @RequestBody ResolveImageMediaRequestDTO request) {

    return ResponseEntity.ok(
            mediaUploadService.getReadyImages(
                    request.getMediaIds(),
                    MediaPurpose.COLLECTION_MEDIA
            )
    );
}

    private CompleteImageUploadResponseDTO mapCompleteResponse(Media media) {
        return CompleteImageUploadResponseDTO.builder()
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
                .uploadedAt(media.getUploadedAt())
                .readyAt(media.getReadyAt())
                .build();
    }
}
