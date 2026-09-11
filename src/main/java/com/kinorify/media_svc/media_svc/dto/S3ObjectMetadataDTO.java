package com.kinorify.media_svc.media_svc.dto;

public record S3ObjectMetadataDTO(
        String storageKey,
        Long sizeBytes,
        String contentType,
        String eTag
) {
}
