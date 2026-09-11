package com.kinorify.media_svc.media_svc.repository;

import com.kinorify.media_svc.media_svc.entity.MediaProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaProcessingJobRepository extends JpaRepository<MediaProcessingJob, UUID> {

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE media_processing_job_id = :jobId
        """, nativeQuery = true)
    Optional<MediaProcessingJob> findProcessingJobById(@Param("jobId") UUID jobId);

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE media_id = :mediaId
        ORDER BY queued_at DESC
        """, nativeQuery = true)
    List<MediaProcessingJob> findProcessingJobsByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE media_id = :mediaId
          AND job_type = :jobType
        ORDER BY queued_at DESC
        """, nativeQuery = true)
    List<MediaProcessingJob> findProcessingJobsByMediaIdAndJobType(@Param("mediaId") UUID mediaId,
            @Param("jobType") String jobType);

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE media_id = :mediaId
          AND job_type = :jobType
          AND status IN ('QUEUED', 'PROCESSING')
        ORDER BY queued_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<MediaProcessingJob> findActiveProcessingJobByMediaIdAndJobType(@Param("mediaId") UUID mediaId,
            @Param("jobType") String jobType);

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE status = :status
        ORDER BY priority ASC, queued_at ASC
        """, nativeQuery = true)
    List<MediaProcessingJob> findProcessingJobsByStatus(@Param("status") String status);

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE status = 'QUEUED'
        ORDER BY priority ASC, queued_at ASC
        """, nativeQuery = true)
    List<MediaProcessingJob> findQueuedProcessingJobs();

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE status = 'FAILED'
          AND attempt_number < max_attempts
        ORDER BY priority ASC, failed_at ASC
        """, nativeQuery = true)
    List<MediaProcessingJob> findRetryableFailedProcessingJobs();

    @Query(value = """
        SELECT *
        FROM media.media_processing_job
        WHERE media_id = :mediaId
          AND status = 'COMPLETED'
        ORDER BY completed_at DESC
        """, nativeQuery = true)
    List<MediaProcessingJob> findCompletedProcessingJobsByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_processing_job
            WHERE media_id = :mediaId
              AND status IN ('QUEUED', 'PROCESSING')
        )
        """, nativeQuery = true)
    boolean existsActiveProcessingJobByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_processing_job
            WHERE media_id = :mediaId
              AND job_type = :jobType
              AND status IN ('QUEUED', 'PROCESSING')
        )
        """, nativeQuery = true)
    boolean existsActiveProcessingJobByMediaIdAndJobType(@Param("mediaId") UUID mediaId,
            @Param("jobType") String jobType);
}
