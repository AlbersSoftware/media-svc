package com.kinorify.media_svc.media_svc.repository;

import com.kinorify.media_svc.media_svc.entity.VideoThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoThumbnailRepository extends JpaRepository<VideoThumbnail, UUID> {

    @Query(value = """
        SELECT *
        FROM media.video_thumbnail
        WHERE video_thumbnail_id = :thumbnailId
        """, nativeQuery = true)
    Optional<VideoThumbnail> findVideoThumbnailById(@Param("thumbnailId") UUID thumbnailId);

    @Query(value = """
        SELECT *
        FROM media.video_thumbnail
        WHERE media_id = :mediaId
        ORDER BY created_at ASC
        """, nativeQuery = true)
    List<VideoThumbnail> findVideoThumbnailsByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.video_thumbnail
        WHERE media_id = :mediaId
          AND thumbnail_type = :thumbnailType
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<VideoThumbnail> findVideoThumbnailsByMediaIdAndType(@Param("mediaId") UUID mediaId,
            @Param("thumbnailType") String thumbnailType);

    @Query(value = """
        SELECT *
        FROM media.video_thumbnail
        WHERE media_id = :mediaId
          AND thumbnail_type = :thumbnailType
        ORDER BY created_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<VideoThumbnail> findLatestVideoThumbnailByMediaIdAndType(@Param("mediaId") UUID mediaId,
            @Param("thumbnailType") String thumbnailType);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.video_thumbnail
            WHERE media_id = :mediaId
        )
        """, nativeQuery = true)
    boolean existsVideoThumbnailByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.video_thumbnail
            WHERE media_id = :mediaId
              AND thumbnail_type = :thumbnailType
        )
        """, nativeQuery = true)
    boolean existsVideoThumbnailByMediaIdAndType(@Param("mediaId") UUID mediaId,
            @Param("thumbnailType") String thumbnailType);
}
