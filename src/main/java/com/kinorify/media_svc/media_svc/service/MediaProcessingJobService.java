package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.MediaProcessingJob;
import com.kinorify.media_svc.media_svc.enums.MediaProcessingJobStatus;

import java.util.List;
import java.util.UUID;

public interface MediaProcessingJobService {

    MediaProcessingJob createProcessingJob(MediaProcessingJob processingJob);

    MediaProcessingJob getProcessingJobById(UUID jobId);

    List<MediaProcessingJob> getProcessingJobsByMediaId(UUID mediaId);

    List<MediaProcessingJob> getProcessingJobsByMediaIdAndJobType(UUID mediaId, String jobType);

    MediaProcessingJob getActiveProcessingJobByMediaIdAndJobType(UUID mediaId, String jobType);

    List<MediaProcessingJob> getProcessingJobsByStatus(MediaProcessingJobStatus status);

    List<MediaProcessingJob> getQueuedProcessingJobs();

    List<MediaProcessingJob> getRetryableFailedProcessingJobs();

    List<MediaProcessingJob> getCompletedProcessingJobsByMediaId(UUID mediaId);

    boolean existsActiveProcessingJobByMediaId(UUID mediaId);

    boolean existsActiveProcessingJobByMediaIdAndJobType(UUID mediaId, String jobType);

    MediaProcessingJob updateProcessingJob(MediaProcessingJob processingJob);

    MediaProcessingJob updateStatus(UUID jobId, MediaProcessingJobStatus status);

    MediaProcessingJob markProcessing(UUID jobId);

    MediaProcessingJob markCompleted(UUID jobId);

    MediaProcessingJob markFailed(UUID jobId, String failureMessage);

    MediaProcessingJob markCancelled(UUID jobId);

    MediaProcessingJob prepareRetry(UUID jobId);
}
