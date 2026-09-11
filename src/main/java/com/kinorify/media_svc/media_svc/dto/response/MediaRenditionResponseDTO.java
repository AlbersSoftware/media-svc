package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.MediaCodec;
import com.kinorify.media_svc.media_svc.enums.MediaRenditionStatus;
import com.kinorify.media_svc.media_svc.enums.MediaStreamType;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaRenditionResponseDTO {

    private UUID mediaRenditionId;

    private UUID mediaId;

    private Integer processingGeneration;

    private MediaStreamType streamType;

    private MediaCodec codec;

    private String container;

    private Integer width;

    private Integer height;

    private BigDecimal frameRate;

    private Long targetBitrateBps;

    private Long averageBitrateBps;

    private Long peakBitrateBps;

    private Long manifestBandwidthBps;

    private String codecProfile;

    private String codecLevel;

    private MediaRenditionStatus status;

    private Short priority;

    private OffsetDateTime startedAt;

    private OffsetDateTime completedAt;

    private OffsetDateTime createdAt;
}
