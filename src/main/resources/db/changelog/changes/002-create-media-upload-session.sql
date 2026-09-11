--liquibase formatted sql

--changeset calbers:002-create-media-upload-session

CREATE TABLE media.media_upload_session (
    media_upload_session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    media_id UUID NOT NULL,

    s3_upload_id VARCHAR(512),

    storage_key VARCHAR(1024) NOT NULL,

    status VARCHAR(30) NOT NULL,

    expected_size_bytes BIGINT NOT NULL,

    part_size_bytes BIGINT,

    content_type VARCHAR(150),

    started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMPTZ,
    aborted_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,

    CONSTRAINT fk_media_upload_session_media
        FOREIGN KEY (media_id)
        REFERENCES media.media(media_id),

    CONSTRAINT chk_media_upload_status
        CHECK (
            status IN (
                'CREATED',
                'UPLOADING',
                'COMPLETED',
                'ABORTED',
                'EXPIRED',
                'FAILED'
            )
        )
);
