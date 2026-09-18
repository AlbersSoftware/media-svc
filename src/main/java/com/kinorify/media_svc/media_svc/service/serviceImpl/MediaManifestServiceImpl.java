package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.dto.response.MediaManifestResponseDTO;
import com.kinorify.media_svc.media_svc.entity.MediaManifest;
import com.kinorify.media_svc.media_svc.enums.MediaManifestStatus;
import com.kinorify.media_svc.media_svc.repository.MediaManifestRepository;
import com.kinorify.media_svc.media_svc.service.MediaManifestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaManifestServiceImpl implements MediaManifestService {

    private final MediaManifestRepository mediaManifestRepository;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Override
    public MediaManifest createManifest(MediaManifest manifest) {
        return mediaManifestRepository.save(manifest);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaManifest getManifestById(UUID manifestId) {
        return mediaManifestRepository.findManifestById(manifestId)
                .orElseThrow(() -> new IllegalStateException(
                        "Media manifest not found: " + manifestId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaManifest> getManifestsByMediaId(UUID mediaId) {
        return mediaManifestRepository.findManifestsByMediaId(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaManifestResponseDTO> getManifestResponsesByMediaId(
            UUID mediaId) {

        return mediaManifestRepository
                .findManifestsByMediaId(mediaId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MediaManifest updateManifest(MediaManifest manifest) {
        getManifestById(manifest.getMediaManifestId());

        return mediaManifestRepository.save(manifest);
    }

    @Override
    public MediaManifest markReady(UUID manifestId) {
        MediaManifest manifest = getManifestById(manifestId);

        if (manifest.getStatus() != MediaManifestStatus.CREATING) {
            throw new IllegalStateException(
                    "Only a creating manifest can be marked ready."
            );
        }

        manifest.setStatus(MediaManifestStatus.READY);

        return mediaManifestRepository.save(manifest);
    }

    @Override
    public MediaManifest markFailed(UUID manifestId) {
        MediaManifest manifest = getManifestById(manifestId);

        if (manifest.getStatus() != MediaManifestStatus.CREATING) {
            throw new IllegalStateException(
                    "Only a creating manifest can be marked failed."
            );
        }

        manifest.setStatus(MediaManifestStatus.FAILED);

        return mediaManifestRepository.save(manifest);
    }

    private MediaManifestResponseDTO mapToResponse(MediaManifest manifest) {

        String manifestUrl =
                "https://"
                        + cloudFrontDomain
                        + "/"
                        + manifest.getStorageKey();

        return MediaManifestResponseDTO.builder()
                .mediaManifestId(
                        manifest.getMediaManifestId()
                )
                .mediaId(manifest.getMediaId())
                .processingGeneration(
                        manifest.getProcessingGeneration()
                )
                .manifestType(
                        manifest.getManifestType()
                )
                .status(manifest.getStatus())
                .version(manifest.getVersion())
                .manifestUrl(manifestUrl)
                .manifestUrlExpiresAt(null)
                .createdAt(manifest.getCreatedAt())
                .updatedAt(manifest.getUpdatedAt())
                .build();
    }
}
