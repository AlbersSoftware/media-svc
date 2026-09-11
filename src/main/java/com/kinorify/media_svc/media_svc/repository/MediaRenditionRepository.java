package com.kinorify.media_svc.media_svc.repository;

import com.kinorify.media_svc.media_svc.entity.MediaRendition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRenditionRepository extends JpaRepository<MediaRendition, UUID> {

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_rendition_id = :renditionId
        """, nativeQuery = true)
    Optional<MediaRendition> findRenditionById(@Param("renditionId") UUID renditionId);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
        ORDER BY processing_generation DESC, priority ASC, created_at ASC
        """, nativeQuery = true)
    List<MediaRendition> findRenditionsByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
        ORDER BY priority ASC, created_at ASC
        """, nativeQuery = true)
    List<MediaRendition> findRenditionsByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND stream_type = :streamType
        ORDER BY priority ASC, created_at ASC
        """, nativeQuery = true)
    List<MediaRendition> findRenditionsByMediaIdGenerationAndStreamType(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration,
            @Param("streamType") String streamType);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND status = :status
        ORDER BY priority ASC, created_at ASC
        """, nativeQuery = true)
    List<MediaRendition> findRenditionsByMediaIdGenerationAndStatus(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration,
            @Param("status") String status);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND status = 'READY'
        ORDER BY priority ASC, created_at ASC
        """, nativeQuery = true)
    List<MediaRendition> findReadyRenditionsByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND stream_type = 'VIDEO'
          AND status = 'READY'
        ORDER BY height ASC, target_bitrate_bps ASC
        """, nativeQuery = true)
    List<MediaRendition> findReadyVideoRenditionsByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND stream_type = 'AUDIO'
          AND status = 'READY'
        ORDER BY target_bitrate_bps ASC
        """, nativeQuery = true)
    List<MediaRendition> findReadyAudioRenditionsByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT *
        FROM media.media_rendition
        WHERE status = :status
        ORDER BY priority ASC, created_at ASC
        """, nativeQuery = true)
    List<MediaRendition> findRenditionsByStatus(@Param("status") String status);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_rendition
            WHERE media_id = :mediaId
              AND processing_generation = :processingGeneration
              AND status = 'READY'
        )
        """, nativeQuery = true)
    boolean existsReadyRenditionByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT COUNT(*)
        FROM media.media_rendition
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND status = :status
        """, nativeQuery = true)
    long countRenditionsByMediaIdGenerationAndStatus(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration,
            @Param("status") String status);
}
