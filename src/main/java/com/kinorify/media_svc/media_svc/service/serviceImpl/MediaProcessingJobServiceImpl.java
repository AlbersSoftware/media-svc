package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.entity.MediaProcessingJob;
import com.kinorify.media_svc.media_svc.enums.MediaProcessingJobStatus;
import com.kinorify.media_svc.media_svc.repository.MediaProcessingJobRepository;
import com.kinorify.media_svc.media_svc.service.MediaProcessingJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaProcessingJobServiceImpl implements MediaProcessingJobService {

    private final MediaProcessingJobRepository mediaProcessingJobRepository;

    @Override
    public MediaProcessingJob createProcessingJob(MediaProcessingJob processingJob) {
        return mediaProcessingJobRepository.save(processingJob);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaProcessingJob getProcessingJobById(UUID jobId) {
        return mediaProcessingJobRepository.findProcessingJobById(jobId)
                .orElseThrow(() -> new IllegalStateException(
                        "Media processing job not found: " + jobId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaProcessingJob> getProcessingJobsByMediaId(UUID mediaId) {
        return mediaProcessingJobRepository.findProcessingJobsByMediaId(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaProcessingJob> getProcessingJobsByMediaIdAndJobType(UUID mediaId, String jobType) {
        return mediaProcessingJobRepository.findProcessingJobsByMediaIdAndJobType(
                mediaId,
                jobType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MediaProcessingJob getActiveProcessingJobByMediaIdAndJobType(UUID mediaId, String jobType) {
        return mediaProcessingJobRepository
                .findActiveProcessingJobByMediaIdAndJobType(
                        mediaId,
                        jobType
                )
                .orElseThrow(() -> new IllegalStateException(
                        "Active media processing job not found for media: "
                                + mediaId
                                + " and job type: "
                                + jobType
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaProcessingJob> getProcessingJobsByStatus(MediaProcessingJobStatus status) {
        return mediaProcessingJobRepository.findProcessingJobsByStatus(
                status.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaProcessingJob> getQueuedProcessingJobs() {
        return mediaProcessingJobRepository.findQueuedProcessingJobs();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaProcessingJob> getRetryableFailedProcessingJobs() {
        return mediaProcessingJobRepository.findRetryableFailedProcessingJobs();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaProcessingJob> getCompletedProcessingJobsByMediaId(UUID mediaId) {
        return mediaProcessingJobRepository.findCompletedProcessingJobsByMediaId(
                mediaId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveProcessingJobByMediaId(UUID mediaId) {
        return mediaProcessingJobRepository.existsActiveProcessingJobByMediaId(
                mediaId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveProcessingJobByMediaIdAndJobType(UUID mediaId, String jobType) {
        return mediaProcessingJobRepository.existsActiveProcessingJobByMediaIdAndJobType(
                mediaId,
                jobType
        );
    }

    @Override
    public MediaProcessingJob updateProcessingJob(MediaProcessingJob processingJob) {
        getProcessingJobById(
                processingJob.getMediaProcessingJobId()
        );

        return mediaProcessingJobRepository.save(
                processingJob
        );
    }

    @Override
    public MediaProcessingJob updateStatus(UUID jobId, MediaProcessingJobStatus status) {
        MediaProcessingJob processingJob =
                getProcessingJobById(jobId);

        processingJob.setStatus(status);

        return mediaProcessingJobRepository.save(
                processingJob
        );
    }

    @Override
    public MediaProcessingJob markProcessing(UUID jobId) {
        MediaProcessingJob processingJob =
                getProcessingJobById(jobId);

        if (processingJob.getStatus() != MediaProcessingJobStatus.QUEUED) {
            throw new IllegalStateException(
                    "Only a queued processing job can begin processing."
            );
        }

        processingJob.setStatus(
                MediaProcessingJobStatus.PROCESSING
        );

        processingJob.setStartedAt(
                OffsetDateTime.now()
        );

        processingJob.setFailureMessage(null);
        processingJob.setFailedAt(null);

        return mediaProcessingJobRepository.save(
                processingJob
        );
    }

    @Override
    public MediaProcessingJob markCompleted(UUID jobId) {
    MediaProcessingJob processingJob =
            getProcessingJobById(jobId);

    if (processingJob.getStatus() != MediaProcessingJobStatus.QUEUED
            && processingJob.getStatus() != MediaProcessingJobStatus.PROCESSING) {

        throw new IllegalStateException(
                "Only a queued or processing job can be completed."
        );
    }

    processingJob.setStatus(
            MediaProcessingJobStatus.COMPLETED
    );

    processingJob.setCompletedAt(
            OffsetDateTime.now()
    );

    processingJob.setFailureMessage(null);
    processingJob.setFailedAt(null);

    return mediaProcessingJobRepository.save(
            processingJob
    );
}

    @Override
    public MediaProcessingJob markFailed(UUID jobId, String failureMessage) {
        MediaProcessingJob processingJob =
                getProcessingJobById(jobId);

        if (processingJob.getStatus() != MediaProcessingJobStatus.QUEUED
                && processingJob.getStatus() != MediaProcessingJobStatus.PROCESSING) {

            throw new IllegalStateException(
                    "Only a queued or processing job can be marked failed."
            );
        }

        processingJob.setStatus(
                MediaProcessingJobStatus.FAILED
        );

        processingJob.setFailureMessage(
                failureMessage
        );

        processingJob.setFailedAt(
                OffsetDateTime.now()
        );

        return mediaProcessingJobRepository.save(
                processingJob
        );
    }

    @Override
    public MediaProcessingJob markCancelled(UUID jobId) {
        MediaProcessingJob processingJob =
                getProcessingJobById(jobId);

        if (processingJob.getStatus() != MediaProcessingJobStatus.QUEUED
                && processingJob.getStatus() != MediaProcessingJobStatus.PROCESSING) {

            throw new IllegalStateException(
                    "Only a queued or processing job can be cancelled."
            );
        }

        processingJob.setStatus(
                MediaProcessingJobStatus.CANCELLED
        );

        return mediaProcessingJobRepository.save(
                processingJob
        );
    }

    @Override
    public MediaProcessingJob prepareRetry(UUID jobId) {
        MediaProcessingJob processingJob =
                getProcessingJobById(jobId);

        if (processingJob.getStatus() != MediaProcessingJobStatus.FAILED) {
            throw new IllegalStateException(
                    "Only a failed processing job can be retried."
            );
        }

        if (processingJob.getAttemptNumber() >= processingJob.getMaxAttempts()) {
            throw new IllegalStateException(
                    "Processing job has reached the maximum number of attempts."
            );
        }

        processingJob.setAttemptNumber(
                processingJob.getAttemptNumber() + 1
        );

        processingJob.setStatus(
                MediaProcessingJobStatus.QUEUED
        );

        processingJob.setStartedAt(null);
        processingJob.setCompletedAt(null);
        processingJob.setFailedAt(null);
        processingJob.setFailureMessage(null);

        return mediaProcessingJobRepository.save(
                processingJob
        );
    }
}
