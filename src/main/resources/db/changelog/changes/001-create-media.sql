--liquibase formatted sql

--changeset calbers:001-create-media

CREATE SCHEMA IF NOT EXISTS media;

CREATE TABLE media.media (
    media_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    uploaded_by_profile_id UUID NOT NULL,

    media_type VARCHAR(20) NOT NULL,
    file_format VARCHAR(30),
    media_purpose VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,

    original_filename VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),

    mime_type VARCHAR(150) NOT NULL,
    size_bytes BIGINT,

    width INT,
    height INT,

    duration_ms BIGINT,

    original_codec VARCHAR(50),
    original_bitrate_bps BIGINT,

    original_storage_key VARCHAR(1024),

    checksum_sha256 VARCHAR(64),

    processing_generation INT NOT NULL DEFAULT 1,

    failure_code VARCHAR(100),
    failure_message TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_at TIMESTAMPTZ,
    processing_started_at TIMESTAMPTZ,
    playable_at TIMESTAMPTZ,
    ready_at TIMESTAMPTZ,
    failed_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_media_type
        CHECK (
            media_type IN (
                'VIDEO',
                'IMAGE',
                'AUDIO',
                'DOCUMENT',
                'ARCHIVE',
                'OTHER'
            )
        ),

    CONSTRAINT chk_media_purpose
        CHECK (
            media_purpose IN (
                'COLLECTION_MEDIA',
                'COLLECTION_THUMBNAIL',
                'PROFILE_AVATAR'
            )
        ),

    CONSTRAINT chk_media_type_purpose
    CHECK (
        media_purpose = 'COLLECTION_MEDIA'
        OR (
            media_type = 'IMAGE'
            AND media_purpose IN (
                'COLLECTION_THUMBNAIL',
                'PROFILE_AVATAR'
            )
        )
    ),

    CONSTRAINT chk_media_status
        CHECK (
            status IN (
                'CREATED',
                'UPLOADING',
                'UPLOADED',
                'PROCESSING',
                'PLAYABLE',
                'READY',
                'PROCESSING_FAILED',
                'RETRYING',
                'TERMINAL_FAILED',
                'ABANDONED',
                'DELETED'
            )
        )
);
