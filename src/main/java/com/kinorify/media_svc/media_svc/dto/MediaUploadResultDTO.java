package com.kinorify.media_svc.media_svc.dto;

import com.kinorify.media_svc.media_svc.entity.Media;
import com.kinorify.media_svc.media_svc.entity.MediaUploadSession;

public record MediaUploadResultDTO(
        Media media,
        MediaUploadSession uploadSession,
        String uploadUrl
) {
}
