--liquibase formatted sql

--changeset calbers:004-create-media-manifest

CREATE TABLE media.media_manifest (
    media_manifest_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    media_id UUID NOT NULL,

    processing_generation INT NOT NULL DEFAULT 1,

    manifest_type VARCHAR(20) NOT NULL,

    storage_key VARCHAR(1024) NOT NULL,

    status VARCHAR(30) NOT NULL,

    version INT NOT NULL DEFAULT 1,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_media_manifest_media
        FOREIGN KEY (media_id)
        REFERENCES media.media(media_id),

    CONSTRAINT chk_media_manifest_generation
        CHECK (
            processing_generation >= 1
        ),

    CONSTRAINT chk_media_manifest_type
        CHECK (
            manifest_type IN (
                'HLS',
                'DASH'
            )
        ),

    CONSTRAINT chk_media_manifest_status
        CHECK (
            status IN (
                'CREATING',
                'READY',
                'FAILED'
            )
        ),

    CONSTRAINT chk_media_manifest_version
        CHECK (
            version >= 1
        ),

    CONSTRAINT uk_media_manifest_media_generation_type
        UNIQUE (
            media_id,
            processing_generation,
            manifest_type
        )
);
