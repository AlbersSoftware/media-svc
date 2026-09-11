--liquibase formatted sql

--changeset calbers:005-create-video-thumbnail

CREATE TABLE media.video_thumbnail (
    video_thumbnail_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    media_id UUID NOT NULL,

    thumbnail_type VARCHAR(30) NOT NULL,

    width INT,
    height INT,

    mime_type VARCHAR(100) NOT NULL,

    storage_key VARCHAR(1024) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_video_thumbnail_media
        FOREIGN KEY (media_id)
        REFERENCES media.media(media_id),

    CONSTRAINT chk_video_thumbnail_type
        CHECK (
            thumbnail_type IN (
                'THUMBNAIL',
                'POSTER',
                'PREVIEW'
            )
        ),

    CONSTRAINT uk_video_thumbnail_media_type
        UNIQUE (
            media_id,
            thumbnail_type
        )
);
