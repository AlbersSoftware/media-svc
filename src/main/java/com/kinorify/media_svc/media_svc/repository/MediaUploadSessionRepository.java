package com.kinorify.media_svc.media_svc.repository;

import com.kinorify.media_svc.media_svc.entity.MediaUploadSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaUploadSessionRepository extends JpaRepository<MediaUploadSession, UUID> {

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE media_upload_session_id = :uploadSessionId
        """, nativeQuery = true)
    Optional<MediaUploadSession> findUploadSessionById(@Param("uploadSessionId") UUID uploadSessionId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE media_id = :mediaId
        ORDER BY started_at DESC
        """, nativeQuery = true)
    List<MediaUploadSession> findUploadSessionsByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE media_id = :mediaId
        ORDER BY started_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<MediaUploadSession> findLatestUploadSessionByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE media_id = :mediaId
          AND status IN ('CREATED', 'UPLOADING')
        ORDER BY started_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<MediaUploadSession> findActiveUploadSessionByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE media_id = :mediaId
          AND status = 'COMPLETED'
        ORDER BY completed_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<MediaUploadSession> findCompletedUploadSessionByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE media_upload_session_id = :uploadSessionId
          AND media_id = :mediaId
        """, nativeQuery = true)
    Optional<MediaUploadSession> findUploadSessionByIdAndMediaId(@Param("uploadSessionId") UUID uploadSessionId,
            @Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE status = :status
        ORDER BY started_at ASC
        """, nativeQuery = true)
    List<MediaUploadSession> findUploadSessionsByStatus(@Param("status") String status);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE s3_upload_id = :s3UploadId
        """, nativeQuery = true)
    Optional<MediaUploadSession> findUploadSessionByS3UploadId(@Param("s3UploadId") String s3UploadId);

    @Query(value = """
        SELECT *
        FROM media.media_upload_session
        WHERE expires_at IS NOT NULL
          AND expires_at <= :now
          AND status IN ('CREATED', 'UPLOADING')
        ORDER BY expires_at ASC
        """, nativeQuery = true)
    List<MediaUploadSession> findExpiredUploadSessions(@Param("now") OffsetDateTime now);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_upload_session
            WHERE media_id = :mediaId
              AND status IN ('CREATED', 'UPLOADING')
        )
        """, nativeQuery = true)
    boolean existsActiveUploadSessionByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_upload_session
            WHERE media_upload_session_id = :uploadSessionId
              AND media_id = :mediaId
              AND status = 'COMPLETED'
        )
        """, nativeQuery = true)
    boolean existsCompletedUploadSession(@Param("uploadSessionId") UUID uploadSessionId,
            @Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_upload_session
            WHERE media_id = :mediaId
              AND status = 'COMPLETED'
        )
        """, nativeQuery = true)
    boolean existsCompletedUploadSessionByMediaId(@Param("mediaId") UUID mediaId);
}
