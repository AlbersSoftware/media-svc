package com.kinorify.media_svc.media_svc.entity;

import com.kinorify.media_svc.media_svc.enums.MediaManifestStatus;
import com.kinorify.media_svc.media_svc.enums.MediaManifestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "media_manifest",
        schema = "media",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_media_manifest_media_generation_type",
                columnNames = {"media_id", "processing_generation", "manifest_type"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaManifest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "media_manifest_id", nullable = false, updatable = false)
    private UUID mediaManifestId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    @Column(name = "processing_generation", nullable = false)
    @Builder.Default
    private Integer processingGeneration = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "manifest_type", nullable = false, length = 20)
    private MediaManifestType manifestType;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private MediaManifestStatus status = MediaManifestStatus.CREATING;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

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

        if (processingGeneration == null) {
            processingGeneration = 1;
        }

        if (status == null) {
            status = MediaManifestStatus.CREATING;
        }

        if (version == null) {
            version = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
