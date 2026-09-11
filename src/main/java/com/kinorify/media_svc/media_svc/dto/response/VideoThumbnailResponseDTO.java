package com.kinorify.media_svc.media_svc.dto.response;

import com.kinorify.media_svc.media_svc.enums.VideoThumbnailType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoThumbnailResponseDTO {

    private UUID videoThumbnailId;

    private UUID mediaId;

    private VideoThumbnailType thumbnailType;

    private Integer width;

    private Integer height;

    private String mimeType;

    private String imageUrl;

    private OffsetDateTime imageUrlExpiresAt;

    private OffsetDateTime createdAt;
}
