package com.kinorify.media_svc.media_svc.entity;

import com.kinorify.media_svc.media_svc.enums.VideoThumbnailType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "video_thumbnail", schema = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoThumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "video_thumbnail_id", nullable = false, updatable = false)
    private UUID videoThumbnailId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    @Column(name = "thumbnail_media_id")
    private UUID thumbnailMediaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "thumbnail_type", nullable = false, length = 30)
    private VideoThumbnailType thumbnailType;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "storage_key", nullable = false, length = 1024)
    private String storageKey;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
