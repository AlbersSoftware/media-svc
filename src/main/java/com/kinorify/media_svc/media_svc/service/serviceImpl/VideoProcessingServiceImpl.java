package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.dto.request.VideoProcessingJobRequestDTO;
import com.kinorify.media_svc.media_svc.dto.VideoManifestResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoProcessingResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoRenditionResultDTO;
import com.kinorify.media_svc.media_svc.dto.VideoRenditionsPeekDTO;
import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.entity.MediaManifest;
import com.kinorify.media_svc.media_svc.entity.MediaProcessingJob;
import com.kinorify.media_svc.media_svc.entity.MediaRendition;
import com.kinorify.media_svc.media_svc.enums.MediaManifestStatus;
import com.kinorify.media_svc.media_svc.enums.MediaManifestType;
import com.kinorify.media_svc.media_svc.enums.MediaProcessingJobStatus;
import com.kinorify.media_svc.media_svc.enums.MediaRenditionStatus;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import com.kinorify.media_svc.media_svc.service.MediaManifestService;
import com.kinorify.media_svc.media_svc.service.MediaProcessingJobService;
import com.kinorify.media_svc.media_svc.service.MediaRenditionService;
import com.kinorify.media_svc.media_svc.service.MediaService;
import com.kinorify.media_svc.media_svc.service.S3StorageService;
import com.kinorify.media_svc.media_svc.service.VideoProcessingQueueService;
import com.kinorify.media_svc.media_svc.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private static final String VIDEO_PIPELINE_JOB_TYPE =
            "VIDEO_PIPELINE";

    private static final Duration VIDEO_DOWNLOAD_DURATION =
            Duration.ofHours(1);

    private final MediaService mediaService;
    private final MediaProcessingJobService mediaProcessingJobService;
    private final MediaRenditionService mediaRenditionService;
    private final MediaManifestService mediaManifestService;
    private final S3StorageService s3StorageService;
    private final VideoProcessingQueueService videoProcessingQueueService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public Media startProcessing(UUID mediaId) {

        Media media =
                mediaService.getMediaById(mediaId);

        validateVideoForProcessing(media);

        if (mediaProcessingJobService
                .existsActiveProcessingJobByMediaIdAndJobType(
                        mediaId,
                        VIDEO_PIPELINE_JOB_TYPE
                )) {

            throw new IllegalStateException(
                    "An active video processing job already exists for media: "
                            + mediaId
            );
        }

        MediaProcessingJob processingJob =
                MediaProcessingJob.builder()
                        .mediaId(mediaId)
                        .processingGeneration(
                                media.getProcessingGeneration()
                        )
                        .jobType(VIDEO_PIPELINE_JOB_TYPE)
                        .status(MediaProcessingJobStatus.QUEUED)
                        .priority((short) 100)
                        .attemptNumber(1)
                        .maxAttempts(3)
                        .build();

        processingJob =
                mediaProcessingJobService.createProcessingJob(
                        processingJob
                );

        mediaService.markProcessing(
                mediaId
        );

        VideoProcessingJobRequestDTO request =
                new VideoProcessingJobRequestDTO(
                        media.getMediaId(),
                        processingJob.getMediaProcessingJobId(),
                        media.getProcessingGeneration(),
                        bucket,
                        media.getOriginalStorageKey(),
                        bucket,
                        getDerivedStoragePrefix(
                                media.getMediaId(),
                                media.getProcessingGeneration()
                        )
                );

        try {

            videoProcessingQueueService.sendProcessingJob(
                    request
            );

            Media readyMedia =
                    mediaService.markReady(
                            mediaId
                    );

            log.info(
                    "Video processing job sent successfully to SQS. mediaId={}, processingJobId={}",
                    mediaId,
                    processingJob.getMediaProcessingJobId()
            );

            return readyMedia;

        } catch (Exception e) {

            mediaProcessingJobService.markFailed(
                    processingJob.getMediaProcessingJobId(),
                    e.getMessage()
            );

            Media failedMedia =
                    mediaService.markFailed(
                            mediaId,
                            "VIDEO_QUEUE_DISPATCH_FAILED",
                            e.getMessage()
                    );

            log.error(
                    "Failed to send video processing job to SQS. mediaId={}, processingJobId={}",
                    mediaId,
                    processingJob.getMediaProcessingJobId(),
                    e
            );

            return failedMedia;
        }
    }
      @Transactional
      @Override
      public Media completeProcessing(
              UUID mediaId,
              UUID processingJobId,
              VideoProcessingResultDTO result) {

          MediaProcessingJob processingJob =
                  mediaProcessingJobService.getProcessingJobById(
                          processingJobId
                  );

          if (!processingJob.getMediaId().equals(mediaId)) {
              throw new IllegalStateException(
                      "Processing job does not belong to the requested media."
              );
          }

          Media media =
                  mediaService.getMediaById(
                          mediaId
                  );

          if (!result.successful()) {

              String failureMessage =
                      result.failureMessage() != null
                              ? result.failureMessage()
                              : "Video worker reported an unspecified processing failure.";

              mediaProcessingJobService.markFailed(
                      processingJobId,
                      failureMessage
              );

              Media failedMedia =
                      mediaService.markFailed(
                              mediaId,
                              "VIDEO_PROCESSING_FAILED",
                              failureMessage
                      );

              log.error(
                      "Video worker reported processing failure. mediaId={}, processingJobId={}, failureMessage={}",
                      mediaId,
                      processingJobId,
                      failureMessage
              );

              return failedMedia;
          }

          validateProcessingResult(
                  media,
                  processingJob,
                  result
          );

          persistRenditions(
                  media,
                  processingJob,
                  result.renditions()
          );

          persistManifests(
                  media,
                  processingJob,
                  result.manifests()
          );

          verifyProcessingOutput(
                  mediaId,
                  processingJob.getProcessingGeneration()
          );

          mediaProcessingJobService.markCompleted(
                  processingJobId
          );

          Media playableMedia =
                  mediaService.markPlayable(
                          mediaId
                  );

          log.info(
                  "Video worker processing completed successfully. Media is now PLAYABLE. mediaId={}, processingJobId={}",
                  mediaId,
                  processingJobId
          );

          return playableMedia;
      }



      // helpers for complete processing


private void validateProcessingResult(
        Media media,
        MediaProcessingJob processingJob,
        VideoProcessingResultDTO result) {

    if (media.getMediaType() != MediaType.VIDEO) {
        throw new IllegalStateException(
                "Processing result can only be applied to video media."
        );
    }

    if (media.getStatus() != MediaStatus.READY) {
        throw new IllegalStateException(
                "Only ready video media can accept a processing result."
        );
    }

    if (processingJob.getStatus() != MediaProcessingJobStatus.QUEUED
            && processingJob.getStatus() != MediaProcessingJobStatus.PROCESSING) {
        throw new IllegalStateException(
                "Processing result can only be applied to a queued or processing job."
        );
    }

    if (!processingJob.getMediaId().equals(media.getMediaId())) {
        throw new IllegalStateException(
                "Processing job does not belong to the requested media."
        );
    }

    if (result.renditions() == null
            || result.renditions().isEmpty()) {
        throw new IllegalStateException(
                "Successful video processing result must contain at least one rendition."
        );
    }

    if (result.manifests() == null
            || result.manifests().isEmpty()) {
        throw new IllegalStateException(
                "Successful video processing result must contain manifests."
        );
    }

    String expectedPrefix =
            getDerivedStoragePrefix(
                    media.getMediaId(),
                    processingJob.getProcessingGeneration()
            );

    for (VideoRenditionResultDTO rendition : result.renditions()) {

        if (rendition.status() != MediaRenditionStatus.READY
                && rendition.status() != MediaRenditionStatus.SKIPPED) {
            throw new IllegalStateException(
                    "Worker may only report READY or SKIPPED renditions."
            );
        }

        if (rendition.storagePrefix() == null
                || !rendition.storagePrefix().startsWith(expectedPrefix)) {
            throw new IllegalStateException(
                    "Rendition storage prefix is outside the expected processing generation."
            );
        }
    }

    boolean hasHlsManifest = false;
    boolean hasDashManifest = false;

    for (VideoManifestResultDTO manifest : result.manifests()) {

        if (manifest.storageKey() == null
                || !manifest.storageKey().startsWith(expectedPrefix)) {
            throw new IllegalStateException(
                    "Manifest storage key is outside the expected processing generation."
            );
        }

        if (manifest.manifestType() == MediaManifestType.HLS) {
            hasHlsManifest = true;
        }

        if (manifest.manifestType() == MediaManifestType.DASH) {
            hasDashManifest = true;
        }
    }

    if (!hasHlsManifest) {
        throw new IllegalStateException(
                "Successful video processing result is missing the HLS manifest."
        );
    }

    if (!hasDashManifest) {
        throw new IllegalStateException(
                "Successful video processing result is missing the DASH manifest."
        );
    }
}

private void persistRenditions(
        Media media,
        MediaProcessingJob processingJob,
        List<VideoRenditionResultDTO> renditionResults) {

    OffsetDateTime completedAt =
            OffsetDateTime.now();

    for (VideoRenditionResultDTO result : renditionResults) {

        MediaRendition rendition =
                new MediaRendition();

        rendition.setMediaId(
                media.getMediaId()
        );

        rendition.setProcessingGeneration(
                processingJob.getProcessingGeneration()
        );

        rendition.setStreamType(
                result.streamType()
        );

        rendition.setCodec(
                result.codec()
        );

        rendition.setContainer(
                result.container()
        );

        rendition.setWidth(
                result.width()
        );

        rendition.setHeight(
                result.height()
        );

        rendition.setFrameRate(
                result.frameRate()
        );

        rendition.setTargetBitrateBps(
                result.targetBitrateBps()
        );

        rendition.setAverageBitrateBps(
                result.averageBitrateBps()
        );

        rendition.setPeakBitrateBps(
                result.peakBitrateBps()
        );

        rendition.setManifestBandwidthBps(
                result.manifestBitrateBps()
        );

        rendition.setCodecProfile(
                result.codecProfile()
        );

        rendition.setCodecLevel(
                result.codecLevel()
        );

        rendition.setStoragePrefix(
                result.storagePrefix()
        );

        rendition.setStatus(
                result.status()
        );

        rendition.setCompletedAt(
                completedAt
        );

        mediaRenditionService.createRendition(
                rendition
        );
    }
}

private void persistManifests(
        Media media,
        MediaProcessingJob processingJob,
        List<VideoManifestResultDTO> manifestResults) {

    for (VideoManifestResultDTO result : manifestResults) {

        MediaManifest manifest =
                new MediaManifest();

        manifest.setMediaId(
                media.getMediaId()
        );

        manifest.setProcessingGeneration(
                processingJob.getProcessingGeneration()
        );

        manifest.setManifestType(
                result.manifestType()
        );

        manifest.setStorageKey(
                result.storageKey()
        );

        manifest.setStatus(
                MediaManifestStatus.READY
        );

        mediaManifestService.createManifest(
                manifest
        );
    }
}


@Override
public VideoRenditionsPeekDTO peekRenditions(UUID mediaId) {

    Media media =
            mediaService.getMediaById(
                    mediaId
            );

    Integer processingGeneration =
            media.getProcessingGeneration();

    String derivedStoragePrefix =
            getDerivedStoragePrefix(
                    mediaId,
                    processingGeneration
            );

    List<String> objectKeys =
            s3StorageService.listObjectKeys(
                    derivedStoragePrefix
            );

    boolean hlsManifestPresent =
            objectKeys.stream()
                    .anyMatch(
                            key -> key.equals(
                                    derivedStoragePrefix
                                            + "package/master.m3u8"
                            )
                    );

    boolean dashManifestPresent =
            objectKeys.stream()
                    .anyMatch(
                            key -> key.equals(
                                    derivedStoragePrefix
                                            + "package/manifest.mpd"
                            )
                    );

    boolean databaseOutputsReady;

    try {

        verifyProcessingOutput(
                mediaId,
                processingGeneration
        );

        databaseOutputsReady = true;

    } catch (RuntimeException exception) {

        databaseOutputsReady = false;
    }

    log.info(
            "Video renditions peek completed. mediaId={}, generation={}, objectCount={}, hlsPresent={}, dashPresent={}, databaseOutputsReady={}, mediaStatus={}",
            mediaId,
            processingGeneration,
            objectKeys.size(),
            hlsManifestPresent,
            dashManifestPresent,
            databaseOutputsReady,
            media.getStatus()
    );

    return new VideoRenditionsPeekDTO(
            mediaId,
            processingGeneration,
            derivedStoragePrefix,
            objectKeys.size(),
            objectKeys,
            hlsManifestPresent,
            dashManifestPresent,
            databaseOutputsReady,
            false,
            media.getStatus()
    );
}


    @Override
    @Transactional(readOnly = true)
    public String getOriginalVideoDownloadUrl(UUID mediaId) {

        Media media =
                mediaService.getMediaById(mediaId);

        if (media.getMediaType() != MediaType.VIDEO) {
            throw new IllegalStateException(
                    "Media is not a video: " + mediaId
            );
        }

        if (media.getOriginalStorageKey() == null
                || media.getOriginalStorageKey().isBlank()) {

            throw new IllegalStateException(
                    "Video does not have an original storage key: "
                            + mediaId
            );
        }

        return s3StorageService.generatePresignedViewUrl(
                media.getOriginalStorageKey(),
                VIDEO_DOWNLOAD_DURATION
        );
    }

    private void validateVideoForProcessing(Media media) {

        if (media.getMediaType() != MediaType.VIDEO) {
            throw new IllegalStateException(
                    "Media is not a video: "
                            + media.getMediaId()
            );
        }

        if (media.getStatus() != MediaStatus.UPLOADED) {
            throw new IllegalStateException(
                    "Only an uploaded video can begin processing."
            );
        }

        if (media.getOriginalStorageKey() == null
                || media.getOriginalStorageKey().isBlank()) {

            throw new IllegalStateException(
                    "Video does not have an original storage key."
            );
        }
    }

    private void verifyProcessingOutput(UUID mediaId,
            Integer processingGeneration) {

        List<MediaRendition> renditions =
                mediaRenditionService.getRenditionsByMediaId(
                        mediaId
                ).stream()
                        .filter(rendition ->
                                processingGeneration.equals(
                                        rendition.getProcessingGeneration()
                                )
                        )
                        .toList();

        if (renditions.isEmpty()) {
            throw new IllegalStateException(
                    "No video renditions found for processing generation: "
                            + processingGeneration
            );
        }

        boolean hasReadyRendition =
                renditions.stream()
                        .anyMatch(rendition ->
                                rendition.getStatus()
                                        == MediaRenditionStatus.READY
                        );

        boolean renditionsComplete =
                renditions.stream()
                        .allMatch(rendition ->
                                rendition.getStatus()
                                        == MediaRenditionStatus.READY
                                        || rendition.getStatus()
                                        == MediaRenditionStatus.SKIPPED
                        );

        if (!hasReadyRendition || !renditionsComplete) {
            throw new IllegalStateException(
                    "Video renditions are not ready for playback."
            );
        }

        List<MediaManifest> manifests =
                mediaManifestService.getManifestsByMediaId(
                        mediaId
                ).stream()
                        .filter(manifest ->
                                processingGeneration.equals(
                                        manifest.getProcessingGeneration()
                                )
                        )
                        .toList();

        boolean hlsReady =
                manifests.stream()
                        .anyMatch(manifest ->
                                manifest.getManifestType()
                                        == MediaManifestType.HLS
                                        && manifest.getStatus()
                                        == MediaManifestStatus.READY
                        );

        boolean dashReady =
                manifests.stream()
                        .anyMatch(manifest ->
                                manifest.getManifestType()
                                        == MediaManifestType.DASH
                                        && manifest.getStatus()
                                        == MediaManifestStatus.READY
                        );

        if (!hlsReady || !dashReady) {
            throw new IllegalStateException(
                    "HLS and DASH manifests must both be ready before video playback."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getDerivedStoragePrefix(UUID mediaId,
            Integer processingGeneration) {

        Media media =
                mediaService.getMediaById(mediaId);

        if (media.getMediaType() != MediaType.VIDEO) {
            throw new IllegalStateException(
                    "Media is not a video: " + mediaId
            );
        }

        if (processingGeneration == null
                || processingGeneration < 1) {

            throw new IllegalArgumentException(
                    "Processing generation must be greater than zero."
            );
        }

        return "derived/video/"
                + mediaId
                + "/generation-"
                + processingGeneration
                + "/";
    }
}
