package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.entity.MediaRendition;
import com.kinorify.media_svc.media_svc.enums.MediaRenditionStatus;
import com.kinorify.media_svc.media_svc.repository.MediaRenditionRepository;
import com.kinorify.media_svc.media_svc.service.MediaRenditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaRenditionServiceImpl implements MediaRenditionService {

    private final MediaRenditionRepository mediaRenditionRepository;

    @Override
    public MediaRendition createRendition(MediaRendition rendition) {
        return mediaRenditionRepository.save(rendition);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaRendition getRenditionById(UUID renditionId) {
        return mediaRenditionRepository.findRenditionById(renditionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Media rendition not found: " + renditionId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaRendition> getRenditionsByMediaId(UUID mediaId) {
        return mediaRenditionRepository.findRenditionsByMediaId(mediaId);
    }

    @Override
    public MediaRendition markProcessing(UUID renditionId) {
        MediaRendition rendition = getRenditionById(renditionId);

        if (rendition.getStatus() != MediaRenditionStatus.QUEUED) {
            throw new IllegalStateException(
                    "Only a queued rendition can begin processing."
            );
        }

        rendition.setStatus(MediaRenditionStatus.PROCESSING);
        rendition.setStartedAt(OffsetDateTime.now());

        return mediaRenditionRepository.save(rendition);
    }

    @Override
    public MediaRendition markReady(UUID renditionId) {
        MediaRendition rendition = getRenditionById(renditionId);

        if (rendition.getStatus() != MediaRenditionStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only a processing rendition can be marked ready."
            );
        }

        rendition.setStatus(MediaRenditionStatus.READY);
        rendition.setCompletedAt(OffsetDateTime.now());

        return mediaRenditionRepository.save(rendition);
    }

    @Override
    public MediaRendition markFailed(UUID renditionId) {
        MediaRendition rendition = getRenditionById(renditionId);

        if (rendition.getStatus() != MediaRenditionStatus.QUEUED
                && rendition.getStatus() != MediaRenditionStatus.PROCESSING) {

            throw new IllegalStateException(
                    "Only a queued or processing rendition can be marked failed."
            );
        }

        rendition.setStatus(MediaRenditionStatus.FAILED);
        rendition.setCompletedAt(OffsetDateTime.now());

        return mediaRenditionRepository.save(rendition);
    }

    @Override
    public MediaRendition markSkipped(UUID renditionId) {
        MediaRendition rendition = getRenditionById(renditionId);

        if (rendition.getStatus() != MediaRenditionStatus.QUEUED) {
            throw new IllegalStateException(
                    "Only a queued rendition can be skipped."
            );
        }

        rendition.setStatus(MediaRenditionStatus.SKIPPED);
        rendition.setCompletedAt(OffsetDateTime.now());

        return mediaRenditionRepository.save(rendition);
    }

    @Override
    public MediaRendition updateRendition(MediaRendition rendition) {
        getRenditionById(rendition.getMediaRenditionId());

        return mediaRenditionRepository.save(rendition);
    }
}
