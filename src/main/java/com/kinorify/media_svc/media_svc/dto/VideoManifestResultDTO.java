package com.kinorify.media_svc.media_svc.dto;

import com.kinorify.media_svc.media_svc.enums.MediaManifestType;

public record VideoManifestResultDTO(
        MediaManifestType manifestType,
        String storageKey
) {
}
