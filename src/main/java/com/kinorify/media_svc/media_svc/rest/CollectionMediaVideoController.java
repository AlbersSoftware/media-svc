package com.kinorify.media_svc.media_svc.controller;

import com.kinorify.media_svc.media_svc.dto.MediaUploadResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoProcessingResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoRenditionsPeekDTO;
import com.kinorify.media_svc.media_svc.dto.response.CreateVideoUploadResponseDTO;
import com.kinorify.media_svc.media_svc.dto.response.CompleteVideoUploadResponseDTO;
import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.entity.MediaManifest;
import com.kinorify.media_svc.media_svc.entity.MediaRendition;
import com.kinorify.media_svc.media_svc.service.MediaManifestService;
import com.kinorify.media_svc.media_svc.service.MediaRenditionService;
import com.kinorify.media_svc.media_svc.service.MediaUploadService;
import com.kinorify.media_svc.media_svc.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kinorify.media_svc.media_svc.dto.response.VideoProcessingStatusResponseDTO;
import com.kinorify.media_svc.media_svc.dto.response.MediaRenditionResponseDTO;
import com.kinorify.media_svc.media_svc.dto.response.MediaManifestResponseDTO;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media/collection-media/videos")
public class CollectionMediaVideoController {

    private final MediaUploadService mediaUploadService;
    private final VideoProcessingService videoProcessingService;
    private final MediaRenditionService mediaRenditionService;
    private final MediaManifestService mediaManifestService;

    /*
     * ORIGINAL VIDEO
     */

   @PostMapping("/uploads")
    public ResponseEntity<CreateVideoUploadResponseDTO> createVideoUpload(
        @RequestParam UUID profileId,
        @RequestParam String originalFilename,
        @RequestParam String mimeType,
        @RequestParam Long expectedSizeBytes) {

    MediaUploadResultDTO result =
            mediaUploadService.createVideoUpload(
                    profileId,
                    originalFilename,
                    mimeType,
                    expectedSizeBytes
            );

    CreateVideoUploadResponseDTO response =
        CreateVideoUploadResponseDTO.builder()
                .mediaId(result.media().getMediaId())
                .uploadSessionId(result.uploadSession().getMediaUploadSessionId())
                .uploadUrl(result.uploadUrl())
                .contentType(result.uploadSession().getContentType())
                .mediaStatus(result.media().getStatus())
                .uploadStatus(result.uploadSession().getStatus())
                .expiresAt(result.uploadSession().getExpiresAt())
                .build();

    return ResponseEntity.ok(response);
}

@PostMapping("/{mediaId}/uploads/{uploadSessionId}/complete")
public ResponseEntity<CompleteVideoUploadResponseDTO> completeVideoUpload(
        @PathVariable UUID mediaId,
        @PathVariable UUID uploadSessionId) {

    Media media =
            mediaUploadService.completeVideoUpload(
                    mediaId,
                    uploadSessionId
            );

    CompleteVideoUploadResponseDTO response =
            CompleteVideoUploadResponseDTO.builder()
                    .mediaId(media.getMediaId())
                    .mediaType(media.getMediaType())
                    .mediaPurpose(media.getMediaPurpose())
                    .status(media.getStatus())
                    .originalFilename(media.getOriginalFilename())
                    .displayName(media.getDisplayName())
                    .fileFormat(media.getFileFormat())
                    .mimeType(media.getMimeType())
                    .sizeBytes(media.getSizeBytes())
                    .processingGeneration(media.getProcessingGeneration())
                    .uploadedAt(media.getUploadedAt())
                    .processingStartedAt(media.getProcessingStartedAt())
                    .build();

    return ResponseEntity.ok(response);
}

@GetMapping("/{mediaId}/original")
public ResponseEntity<String> getOriginalVideo(
        @PathVariable UUID mediaId) {

    String originalUrl =
            videoProcessingService.getOriginalVideoDownloadUrl(
                    mediaId
            );

    return ResponseEntity.ok(originalUrl);
}

@GetMapping("/{mediaId}/status")
public ResponseEntity<VideoProcessingStatusResponseDTO> getVideoProcessingStatus(
        @PathVariable UUID mediaId) {

    VideoProcessingStatusResponseDTO status =
            videoProcessingService.getProcessingStatus(
                    mediaId
            );

    return ResponseEntity.ok(status);
}

    /*
     * DERIVED RENDITIONS
     */

    @GetMapping("/{mediaId}/renditions")
public ResponseEntity<List<MediaRenditionResponseDTO>> getVideoRenditions(
        @PathVariable UUID mediaId) {

    List<MediaRenditionResponseDTO> renditions =
            mediaRenditionService
                    .getRenditionsByMediaId(
                            mediaId
                    )
                    .stream()
                    .map(rendition ->
                            MediaRenditionResponseDTO.builder()
                                    .mediaRenditionId(
                                            rendition.getMediaRenditionId()
                                    )
                                    .mediaId(rendition.getMediaId())
                                    .processingGeneration(
                                            rendition.getProcessingGeneration()
                                    )
                                    .streamType(rendition.getStreamType())
                                    .codec(rendition.getCodec())
                                    .container(rendition.getContainer())
                                    .width(rendition.getWidth())
                                    .height(rendition.getHeight())
                                    .frameRate(rendition.getFrameRate())
                                    .targetBitrateBps(
                                            rendition.getTargetBitrateBps()
                                    )
                                    .averageBitrateBps(
                                            rendition.getAverageBitrateBps()
                                    )
                                    .peakBitrateBps(
                                            rendition.getPeakBitrateBps()
                                    )
                                    .manifestBandwidthBps(
                                            rendition.getManifestBandwidthBps()
                                    )
                                    .codecProfile(
                                            rendition.getCodecProfile()
                                    )
                                    .codecLevel(
                                            rendition.getCodecLevel()
                                    )
                                    .status(rendition.getStatus())
                                    .priority(rendition.getPriority())
                                    .startedAt(rendition.getStartedAt())
                                    .completedAt(rendition.getCompletedAt())
                                    .createdAt(rendition.getCreatedAt())
                                    .build()
                    )
                    .toList();

    return ResponseEntity.ok(renditions);
}

    @GetMapping("/{mediaId}/manifests")
    public ResponseEntity<List<MediaManifestResponseDTO>> getVideoManifests(
        @PathVariable UUID mediaId) {

    List<MediaManifestResponseDTO> manifests =
            mediaManifestService
                    .getManifestResponsesByMediaId(
                            mediaId
                    );

    return ResponseEntity.ok(manifests);
}

    @GetMapping("/{mediaId}/renditions-peek")
    public ResponseEntity<VideoRenditionsPeekDTO> peekVideoRenditions(
            @PathVariable UUID mediaId) {

        VideoRenditionsPeekDTO result =
                videoProcessingService.peekRenditions(
                        mediaId
                );

        return ResponseEntity.ok(result);
    }

    /*
     * WORKER CALLBACK
     */

    @PostMapping("/{mediaId}/processing-jobs/{processingJobId}/result")
    public ResponseEntity<Media> completeVideoProcessing(
            @PathVariable UUID mediaId,
            @PathVariable UUID processingJobId,
            @RequestBody VideoProcessingResultDTO result) {

        Media media =
                videoProcessingService.completeProcessing(
                        mediaId,
                        processingJobId,
                        result
                );

        return ResponseEntity.ok(media);
    }
}
