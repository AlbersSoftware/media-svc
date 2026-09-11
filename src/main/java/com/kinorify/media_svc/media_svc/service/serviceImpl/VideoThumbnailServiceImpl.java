package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.entity.VideoThumbnail;
import com.kinorify.media_svc.media_svc.repository.VideoThumbnailRepository;
import com.kinorify.media_svc.media_svc.service.VideoThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoThumbnailServiceImpl implements VideoThumbnailService {

    private final VideoThumbnailRepository videoThumbnailRepository;

    @Override
    public VideoThumbnail createVideoThumbnail(VideoThumbnail thumbnail) {
        return videoThumbnailRepository.save(thumbnail);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoThumbnail getVideoThumbnailById(UUID thumbnailId) {
        return videoThumbnailRepository.findVideoThumbnailById(thumbnailId)
                .orElseThrow(() -> new IllegalStateException(
                        "Video thumbnail not found: " + thumbnailId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoThumbnail> getVideoThumbnailsByMediaId(UUID mediaId) {
        return videoThumbnailRepository.findVideoThumbnailsByMediaId(mediaId);
    }
}
