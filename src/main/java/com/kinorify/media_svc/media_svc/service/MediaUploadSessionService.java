package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.MediaUploadSession;
import com.kinorify.media_svc.media_svc.enums.MediaUploadStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MediaUploadSessionService {

    MediaUploadSession createUploadSession(MediaUploadSession uploadSession);

    MediaUploadSession getUploadSessionById(UUID uploadSessionId);

    List<MediaUploadSession> getUploadSessionsByMediaId(UUID mediaId);

    MediaUploadSession getActiveUploadSessionByMediaId(UUID mediaId);

    MediaUploadSession getUploadSessionByIdAndMediaId(UUID uploadSessionId, UUID mediaId);

    List<MediaUploadSession> getUploadSessionsByStatus(MediaUploadStatus status);

    MediaUploadSession getUploadSessionByS3UploadId(String s3UploadId);

    List<MediaUploadSession> getExpiredUploadSessions(OffsetDateTime now);

    boolean existsActiveUploadSessionByMediaId(UUID mediaId);

    boolean existsCompletedUploadSession(UUID uploadSessionId, UUID mediaId);

    MediaUploadSession updateUploadSession(MediaUploadSession uploadSession);

    MediaUploadSession updateStatus(UUID uploadSessionId, MediaUploadStatus status);

    MediaUploadSession markUploading(UUID uploadSessionId);

    MediaUploadSession markCompleted(UUID uploadSessionId);

    MediaUploadSession markAborted(UUID uploadSessionId);

    MediaUploadSession markExpired(UUID uploadSessionId);

    MediaUploadSession markFailed(UUID uploadSessionId);
}
