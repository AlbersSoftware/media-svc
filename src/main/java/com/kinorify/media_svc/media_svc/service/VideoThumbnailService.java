package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.VideoThumbnail;
import com.kinorify.media_svc.media_svc.enums.VideoThumbnailType;

import java.util.List;
import java.util.UUID;

public interface VideoThumbnailService {

    void validateVideoMedia(UUID mediaId);

    VideoThumbnail setVideoThumbnail(UUID mediaId, UUID thumbnailMediaId, VideoThumbnailType thumbnailType);

    VideoThumbnail getVideoThumbnailById(UUID thumbnailId);

    List<VideoThumbnail> getVideoThumbnailsByMediaId(UUID mediaId);

    VideoThumbnail getVideoThumbnailByMediaIdAndType(UUID mediaId, VideoThumbnailType thumbnailType);
}
