package com.kinorify.media_svc.media_svc.dto;

import com.kinorify.media_svc.media_svc.enums.MediaCodec;
import com.kinorify.media_svc.media_svc.enums.MediaRenditionStatus;
import com.kinorify.media_svc.media_svc.enums.MediaStreamType;

import java.math.BigDecimal;

public record VideoRenditionResultDTO(
        MediaStreamType streamType,
        MediaCodec codec,
        String container,
        Integer width,
        Integer height,
        BigDecimal frameRate,
        Long targetBitrateBps,
        Long averageBitrateBps,
        Long peakBitrateBps,
        Long manifestBitrateBps,
        String codecProfile,
        String codecLevel,
        String storagePrefix,
        MediaRenditionStatus status
) {
}
