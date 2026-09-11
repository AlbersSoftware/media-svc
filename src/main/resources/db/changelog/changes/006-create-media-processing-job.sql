--liquibase formatted sql

--changeset calbers:006-create-media-processing-job

CREATE TABLE media.media_processing_job (
    media_processing_job_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    media_id UUID NOT NULL,

    job_type VARCHAR(40) NOT NULL,

    status VARCHAR(30) NOT NULL,

    priority SMALLINT NOT NULL DEFAULT 100,

    attempt_number INT NOT NULL DEFAULT 1,
    max_attempts INT NOT NULL DEFAULT 3,

    command_summary TEXT,

    failure_message TEXT,

    queued_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    failed_at TIMESTAMPTZ,

    CONSTRAINT fk_media_processing_job_media
        FOREIGN KEY (media_id)
        REFERENCES media.media(media_id),

    CONSTRAINT chk_media_processing_job_status
        CHECK (
            status IN (
                'QUEUED',
                'PROCESSING',
                'COMPLETED',
                'FAILED',
                'CANCELLED'
            )
        )
);
