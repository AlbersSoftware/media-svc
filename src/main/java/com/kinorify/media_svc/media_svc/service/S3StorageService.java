package com.kinorify.media_svc.media_svc.service;

import com.kinorify.media_svc.media_svc.dto.S3ObjectMetadataDTO;
import java.util.List;
import java.time.Duration;

public interface S3StorageService {

    String generatePresignedUploadUrl(String storageKey, String contentType, Duration duration);

    String generatePresignedViewUrl(String storageKey, Duration duration);

    S3ObjectMetadataDTO getObjectMetadata(String storageKey);

    boolean objectExists(String storageKey);

    List<String> listObjectKeys(String storagePrefix);

    void deleteObject(String storageKey);
}
