package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.entity.MediaUploadSession;
import com.kinorify.media_svc.media_svc.enums.MediaUploadStatus;
import com.kinorify.media_svc.media_svc.repository.MediaUploadSessionRepository;
import com.kinorify.media_svc.media_svc.service.MediaUploadSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaUploadSessionServiceImpl implements MediaUploadSessionService {

    private final MediaUploadSessionRepository mediaUploadSessionRepository;

    @Override
    public MediaUploadSession createUploadSession(MediaUploadSession uploadSession) {
        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaUploadSession getUploadSessionById(UUID uploadSessionId) {
        return mediaUploadSessionRepository.findUploadSessionById(uploadSessionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Media upload session not found: " + uploadSessionId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaUploadSession> getUploadSessionsByMediaId(UUID mediaId) {
        return mediaUploadSessionRepository.findUploadSessionsByMediaId(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaUploadSession getActiveUploadSessionByMediaId(UUID mediaId) {
        return mediaUploadSessionRepository.findActiveUploadSessionByMediaId(mediaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Active media upload session not found for media: " + mediaId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public MediaUploadSession getUploadSessionByIdAndMediaId(UUID uploadSessionId, UUID mediaId) {
        return mediaUploadSessionRepository.findUploadSessionByIdAndMediaId(
                        uploadSessionId,
                        mediaId
                )
                .orElseThrow(() -> new IllegalStateException(
                        "Media upload session not found for media: " + mediaId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaUploadSession> getUploadSessionsByStatus(MediaUploadStatus status) {
        return mediaUploadSessionRepository.findUploadSessionsByStatus(
                status.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MediaUploadSession getUploadSessionByS3UploadId(String s3UploadId) {
        return mediaUploadSessionRepository.findUploadSessionByS3UploadId(s3UploadId)
                .orElseThrow(() -> new IllegalStateException(
                        "Media upload session not found for S3 upload ID: " + s3UploadId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaUploadSession> getExpiredUploadSessions(OffsetDateTime now) {
        return mediaUploadSessionRepository.findExpiredUploadSessions(now);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveUploadSessionByMediaId(UUID mediaId) {
        return mediaUploadSessionRepository.existsActiveUploadSessionByMediaId(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCompletedUploadSession(UUID uploadSessionId, UUID mediaId) {
        return mediaUploadSessionRepository.existsCompletedUploadSession(
                uploadSessionId,
                mediaId
        );
    }

    @Override
    public MediaUploadSession updateUploadSession(MediaUploadSession uploadSession) {
        getUploadSessionById(uploadSession.getMediaUploadSessionId());

        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    public MediaUploadSession updateStatus(UUID uploadSessionId, MediaUploadStatus status) {
        MediaUploadSession uploadSession = getUploadSessionById(uploadSessionId);

        uploadSession.setStatus(status);

        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    public MediaUploadSession markUploading(UUID uploadSessionId) {
        MediaUploadSession uploadSession = getUploadSessionById(uploadSessionId);

        uploadSession.setStatus(MediaUploadStatus.UPLOADING);

        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    public MediaUploadSession markCompleted(UUID uploadSessionId) {
        MediaUploadSession uploadSession = getUploadSessionById(uploadSessionId);

        uploadSession.setStatus(MediaUploadStatus.COMPLETED);
        uploadSession.setCompletedAt(OffsetDateTime.now());

        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    public MediaUploadSession markAborted(UUID uploadSessionId) {
        MediaUploadSession uploadSession = getUploadSessionById(uploadSessionId);

        uploadSession.setStatus(MediaUploadStatus.ABORTED);
        uploadSession.setAbortedAt(OffsetDateTime.now());

        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    public MediaUploadSession markExpired(UUID uploadSessionId) {
        MediaUploadSession uploadSession = getUploadSessionById(uploadSessionId);

        uploadSession.setStatus(MediaUploadStatus.EXPIRED);

        return mediaUploadSessionRepository.save(uploadSession);
    }

    @Override
    public MediaUploadSession markFailed(UUID uploadSessionId) {
        MediaUploadSession uploadSession = getUploadSessionById(uploadSessionId);

        uploadSession.setStatus(MediaUploadStatus.FAILED);

        return mediaUploadSessionRepository.save(uploadSession);
    }
}
