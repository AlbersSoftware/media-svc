package com.kinorify.media_svc.media_svc.entity;

import com.kinorify.media_svc.media_svc.enums.MediaProcessingJobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_processing_job", schema = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaProcessingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "media_processing_job_id", nullable = false, updatable = false)
    private UUID mediaProcessingJobId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;
    
    @Column(name = "processing_generation", nullable = false)
    @Builder.Default
    private Integer processingGeneration = 1;
    /*
     * Intentionally String-based because processing job types
     * will evolve as the processing pipelines become more granular.
     */
    @Column(name = "job_type", nullable = false, length = 40)
    private String jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private MediaProcessingJobStatus status = MediaProcessingJobStatus.QUEUED;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Short priority = 100;

    @Column(name = "attempt_number", nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    @Column(name = "max_attempts", nullable = false)
    @Builder.Default
    private Integer maxAttempts = 3;

    @Column(name = "command_summary", columnDefinition = "TEXT")
    private String commandSummary;

    @Column(name = "failure_message", columnDefinition = "TEXT")
    private String failureMessage;

    @Column(name = "queued_at", nullable = false, updatable = false)
    private OffsetDateTime queuedAt;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    @PrePersist
    protected void onCreate() {
        if (queuedAt == null) {
            queuedAt = OffsetDateTime.now();
        }

        if (processingGeneration == null) {
            processingGeneration = 1;
        }

        if (status == null) {
            status = MediaProcessingJobStatus.QUEUED;
        }

        if (priority == null) {
            priority = 100;
        }

        if (attemptNumber == null) {
            attemptNumber = 1;
        }

        if (maxAttempts == null) {
            maxAttempts = 3;
        }
    }
}
