--liquibase formatted sql

--changeset calbers:008-update-media-processing-job

ALTER TABLE media.media_processing_job
    ADD COLUMN processing_generation INTEGER NOT NULL DEFAULT 1;

--rollback ALTER TABLE media.media_processing_job DROP COLUMN processing_generation;
