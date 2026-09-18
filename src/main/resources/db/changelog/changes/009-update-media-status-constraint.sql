--liquibase formatted sql

--changeset calbers:009-update-media-status-constraint

ALTER TABLE media.media
DROP CONSTRAINT chk_media_status;

ALTER TABLE media.media
ADD CONSTRAINT chk_media_status
CHECK (
    status IN (
        'CREATED',
        'UPLOADING',
        'UPLOADED',
        'PROCESSING',
        'PLAYABLE',
        'READY',
        'NO_RENDITIONS_NEEDED',
        'SQS_FAILED',
        'RENDITIONS_FAILED',
        'MEDIA_CALLBACK_FAILED',
        'S3_UPLOAD_FAILED',
        'PROCESSING_FAILED',
        'RETRYING',
        'TERMINAL_FAILED',
        'ABANDONED',
        'DELETED'
    )
);
