--liquibase formatted sql

--changeset calbers:003-create-media-rendition

CREATE TABLE media.media_rendition (
    media_rendition_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    media_id UUID NOT NULL,

    processing_generation INT NOT NULL DEFAULT 1,

    stream_type VARCHAR(20) NOT NULL,

    codec VARCHAR(30) NOT NULL,

    container VARCHAR(30) NOT NULL,

    width INT,
    height INT,

    frame_rate NUMERIC(8,3),

    target_bitrate_bps BIGINT,
    average_bitrate_bps BIGINT,
    peak_bitrate_bps BIGINT,
    manifest_bandwidth_bps BIGINT,

    codec_profile VARCHAR(50),
    codec_level VARCHAR(50),

    storage_prefix VARCHAR(1024) NOT NULL,

    status VARCHAR(30) NOT NULL,

    priority SMALLINT NOT NULL DEFAULT 100,

    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_media_rendition_media
        FOREIGN KEY (media_id)
        REFERENCES media.media(media_id),

    CONSTRAINT chk_media_rendition_generation
        CHECK (
            processing_generation >= 1
        ),

    CONSTRAINT chk_media_rendition_stream_type
        CHECK (
            stream_type IN (
                'VIDEO',
                'AUDIO'
            )
        ),

    CONSTRAINT chk_media_rendition_codec
        CHECK (
            codec IN (
                'H264',
                'HEVC',
                'AV1',
                'AAC',
                'OPUS'
            )
        ),

    CONSTRAINT chk_media_rendition_status
        CHECK (
            status IN (
                'QUEUED',
                'PROCESSING',
                'READY',
                'FAILED',
                'SKIPPED'
            )
        )
);
