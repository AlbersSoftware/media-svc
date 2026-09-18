--liquibase formatted sql

--changeset calbers:010-update-video-thumbnail

ALTER TABLE media.video_thumbnail
    ADD COLUMN thumbnail_media_id UUID,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE media.video_thumbnail
    ADD CONSTRAINT fk_video_thumbnail_thumbnail_media
        FOREIGN KEY (thumbnail_media_id)
        REFERENCES media.media(media_id);
