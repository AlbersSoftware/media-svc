package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.MediaRendition;

import java.util.List;
import java.util.UUID;

public interface MediaRenditionService {

    MediaRendition createRendition(MediaRendition rendition);

    MediaRendition getRenditionById(UUID renditionId);

    List<MediaRendition> getRenditionsByMediaId(UUID mediaId);

    MediaRendition markProcessing(UUID renditionId);

    MediaRendition markReady(UUID renditionId);

    MediaRendition markFailed(UUID renditionId);

    MediaRendition markSkipped(UUID renditionId);

    MediaRendition updateRendition(MediaRendition rendition);
}
