package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.VideoThumbnail;

import java.util.List;
import java.util.UUID;

public interface VideoThumbnailService {

    VideoThumbnail createVideoThumbnail(VideoThumbnail thumbnail);

    VideoThumbnail getVideoThumbnailById(UUID thumbnailId);

    List<VideoThumbnail> getVideoThumbnailsByMediaId(UUID mediaId);
}
