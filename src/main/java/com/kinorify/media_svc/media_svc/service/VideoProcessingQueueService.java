package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.dto.request.VideoProcessingJobRequestDTO;

public interface VideoProcessingQueueService {

    void sendProcessingJob(VideoProcessingJobRequestDTO request);
}
