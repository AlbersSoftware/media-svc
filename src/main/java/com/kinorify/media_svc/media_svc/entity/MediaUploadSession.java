package com.kinorify.media_svc.media_svc.entity;

import com.kinorify.media_svc.media_svc.enums.MediaUploadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_upload_session", schema = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaUploadSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "media_upload_session_id", nullable = false, updatable = false)
    private UUID mediaUploadSessionId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    /*
     * S3 multipart upload ID.
     * May remain null until a multipart upload has been initialized.
     */
    @Column(name = "s3_upload_id", length = 512)
    private String s3UploadId;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private MediaUploadStatus status = MediaUploadStatus.CREATED;

    @Column(name = "expected_size_bytes", nullable = false)
    private Long expectedSizeBytes;

    @Column(name = "part_size_bytes")
    private Long partSizeBytes;

    @Column(name = "content_type", length = 150)
    private String contentType;

    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "aborted_at")
    private OffsetDateTime abortedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = OffsetDateTime.now();
        }

        if (status == null) {
            status = MediaUploadStatus.CREATED;
        }
    }
}
