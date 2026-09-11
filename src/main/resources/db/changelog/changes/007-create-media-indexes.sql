--liquibase formatted sql

--changeset calbers:007-create-media-indexes


-- -------------------------------------------------------------------------
-- Media
-- -------------------------------------------------------------------------

CREATE INDEX idx_media_uploaded_by_profile_id
    ON media.media (
        uploaded_by_profile_id
    );

CREATE INDEX idx_media_type
    ON media.media (
        media_type
    );

CREATE INDEX idx_media_purpose
    ON media.media (
        media_purpose
    );

CREATE INDEX idx_media_status
    ON media.media (
        status
    );

CREATE INDEX idx_media_created_at
    ON media.media (
        created_at DESC
    );


-- -------------------------------------------------------------------------
-- Media upload sessions
-- -------------------------------------------------------------------------

CREATE INDEX idx_media_upload_session_media_id
    ON media.media_upload_session (
        media_id
    );

CREATE INDEX idx_media_upload_session_status
    ON media.media_upload_session (
        status
    );


-- -------------------------------------------------------------------------
-- Media renditions
-- -------------------------------------------------------------------------

CREATE INDEX idx_media_rendition_media_status
    ON media.media_rendition (
        media_id,
        status
    );

CREATE INDEX idx_media_rendition_generation_status
    ON media.media_rendition (
        media_id,
        processing_generation,
        status
    );


-- -------------------------------------------------------------------------
-- Media manifests
-- -------------------------------------------------------------------------

CREATE INDEX idx_media_manifest_media_generation
    ON media.media_manifest (
        media_id,
        processing_generation
    );


-- -------------------------------------------------------------------------
-- Video thumbnails
-- -------------------------------------------------------------------------

CREATE INDEX idx_video_thumbnail_media_id
    ON media.video_thumbnail (
        media_id
    );


-- -------------------------------------------------------------------------
-- Media processing jobs
-- -------------------------------------------------------------------------

CREATE INDEX idx_media_processing_job_media_id
    ON media.media_processing_job (
        media_id
    );

CREATE INDEX idx_media_processing_job_status_priority
    ON media.media_processing_job (
        status,
        priority,
        queued_at
    );
