package com.kinorify.media_svc.media_svc.controller;

import com.kinorify.media_svc.media_svc.dto.MediaUploadResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoProcessingResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoRenditionsPeekDTO;
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
    public ResponseEntity<MediaUploadResultDTO> createVideoUpload(
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

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{mediaId}/uploads/{uploadSessionId}/complete")
    public ResponseEntity<Media> completeVideoUpload(
            @PathVariable UUID mediaId,
            @PathVariable UUID uploadSessionId) {

        Media media =
                mediaUploadService.completeVideoUpload(
                        mediaId,
                        uploadSessionId
                );

        return ResponseEntity.ok(media);
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

    /*
     * DERIVED RENDITIONS
     */

    @GetMapping("/{mediaId}/renditions")
    public ResponseEntity<List<MediaRendition>> getVideoRenditions(
            @PathVariable UUID mediaId) {

        List<MediaRendition> renditions =
                mediaRenditionService.getRenditionsByMediaId(
                        mediaId
                );

        return ResponseEntity.ok(renditions);
    }

    @GetMapping("/{mediaId}/manifests")
    public ResponseEntity<List<MediaManifest>> getVideoManifests(
            @PathVariable UUID mediaId) {

        List<MediaManifest> manifests =
                mediaManifestService.getManifestsByMediaId(
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
