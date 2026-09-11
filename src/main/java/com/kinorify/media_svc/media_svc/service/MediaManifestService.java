package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.entity.MediaManifest;

import java.util.List;
import java.util.UUID;

public interface MediaManifestService {

    MediaManifest createManifest(MediaManifest manifest);

    MediaManifest getManifestById(UUID manifestId);

    List<MediaManifest> getManifestsByMediaId(UUID mediaId);

    MediaManifest updateManifest(MediaManifest manifest);

    MediaManifest markReady(UUID manifestId);

    MediaManifest markFailed(UUID manifestId);
}
