package com.kinorify.media_svc.media_svc.entity;

import com.kinorify.media_svc.media_svc.enums.MediaCodec;
import com.kinorify.media_svc.media_svc.enums.MediaRenditionStatus;
import com.kinorify.media_svc.media_svc.enums.MediaStreamType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_rendition", schema = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaRendition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "media_rendition_id", nullable = false, updatable = false)
    private UUID mediaRenditionId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;

    @Column(name = "processing_generation", nullable = false)
    @Builder.Default
    private Integer processingGeneration = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "stream_type", nullable = false, length = 20)
    private MediaStreamType streamType;

    @Enumerated(EnumType.STRING)
    @Column(name = "codec", nullable = false, length = 30)
    private MediaCodec codec;

    @Column(name = "container", nullable = false, length = 30)
    private String container;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "frame_rate", precision = 8, scale = 3)
    private BigDecimal frameRate;

    @Column(name = "target_bitrate_bps")
    private Long targetBitrateBps;

    @Column(name = "average_bitrate_bps")
    private Long averageBitrateBps;

    @Column(name = "peak_bitrate_bps")
    private Long peakBitrateBps;

    @Column(name = "manifest_bandwidth_bps")
    private Long manifestBandwidthBps;

    @Column(name = "codec_profile", length = 50)
    private String codecProfile;

    @Column(name = "codec_level", length = 50)
    private String codecLevel;

    @Column(name = "storage_prefix", nullable = false, length = 1024)
    private String storagePrefix;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private MediaRenditionStatus status = MediaRenditionStatus.QUEUED;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Short priority = 100;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }

        if (processingGeneration == null) {
            processingGeneration = 1;
        }

        if (status == null) {
            status = MediaRenditionStatus.QUEUED;
        }

        if (priority == null) {
            priority = 100;
        }
    }
}
