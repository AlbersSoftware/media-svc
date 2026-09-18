--liquibase formatted sql

--changeset calbers:011-add-video-thumbnail-media-purpose

ALTER TABLE media.media
    DROP CONSTRAINT chk_media_purpose;

ALTER TABLE media.media
    ADD CONSTRAINT chk_media_purpose
        CHECK (
            media_purpose IN (
                'COLLECTION_MEDIA',
                'COLLECTION_THUMBNAIL',
                'PROFILE_AVATAR',
                'VIDEO_THUMBNAIL'
            )
        );

ALTER TABLE media.media
    DROP CONSTRAINT chk_media_type_purpose;

ALTER TABLE media.media
    ADD CONSTRAINT chk_media_type_purpose
        CHECK (
            media_purpose = 'COLLECTION_MEDIA'
            OR (
                media_type = 'IMAGE'
                AND media_purpose IN (
                    'COLLECTION_THUMBNAIL',
                    'PROFILE_AVATAR',
                    'VIDEO_THUMBNAIL'
                )
            )
        );
