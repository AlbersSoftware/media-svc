package com.kinorify.media_svc.media_svc.entity;

import com.kinorify.media_svc.media_svc.enums.MediaPurpose;
import com.kinorify.media_svc.media_svc.enums.MediaStatus;
import com.kinorify.media_svc.media_svc.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media", schema = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "media_id", nullable = false, updatable = false)
    private UUID mediaId;

    @Column(name = "uploaded_by_profile_id", nullable = false)
    private UUID uploadedByProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType;

    /*
     * Intentionally stored as String rather than a DB-constrained enum.
     * New file formats should not require a database migration.
     */
    @Column(name = "file_format", length = 30)
    private String fileFormat;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_purpose", nullable = false, length = 30)
    private MediaPurpose mediaPurpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private MediaStatus status = MediaStatus.CREATED;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "mime_type", nullable = false, length = 150)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "original_codec", length = 50)
    private String originalCodec;

    @Column(name = "original_bitrate_bps")
    private Long originalBitrateBps;

    @Column(name = "original_storage_key", length = 1024)
    private String originalStorageKey;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "processing_generation", nullable = false)
    @Builder.Default
    private Integer processingGeneration = 1;

    @Column(name = "failure_code", length = 100)
    private String failureCode;

    @Column(name = "failure_message", columnDefinition = "TEXT")
    private String failureMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "uploaded_at")
    private OffsetDateTime uploadedAt;

    @Column(name = "processing_started_at")
    private OffsetDateTime processingStartedAt;

    @Column(name = "playable_at")
    private OffsetDateTime playableAt;

    @Column(name = "ready_at")
    private OffsetDateTime readyAt;

    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = MediaStatus.CREATED;
        }

        if (processingGeneration == null) {
            processingGeneration = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
