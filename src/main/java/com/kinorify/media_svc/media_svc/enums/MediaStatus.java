package com.kinorify.media_svc.media_svc.enums;

public enum MediaStatus {
    CREATED,
    UPLOADING,
    UPLOADED,
    PROCESSING,
    PLAYABLE,
    READY,
    NO_RENDITIONS_NEEDED,
    SQS_FAILED,
    RENDITIONS_FAILED,
    MEDIA_CALLBACK_FAILED,
    S3_UPLOAD_FAILED,
    PROCESSING_FAILED,
    RETRYING,
    TERMINAL_FAILED,
    ABANDONED,
    DELETED
}
