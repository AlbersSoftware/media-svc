package com.kinorify.media_svc.media_svc.repository;

import com.kinorify.media_svc.media_svc.entity.MediaManifest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaManifestRepository extends JpaRepository<MediaManifest, UUID> {

    @Query(value = """
        SELECT *
        FROM media.media_manifest
        WHERE media_manifest_id = :manifestId
        """, nativeQuery = true)
    Optional<MediaManifest> findManifestById(@Param("manifestId") UUID manifestId);

    @Query(value = """
        SELECT *
        FROM media.media_manifest
        WHERE media_id = :mediaId
        ORDER BY processing_generation DESC, created_at DESC
        """, nativeQuery = true)
    List<MediaManifest> findManifestsByMediaId(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media_manifest
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
        ORDER BY manifest_type ASC
        """, nativeQuery = true)
    List<MediaManifest> findManifestsByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT *
        FROM media.media_manifest
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND manifest_type = :manifestType
        """, nativeQuery = true)
    Optional<MediaManifest> findManifestByMediaIdGenerationAndType(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration,
            @Param("manifestType") String manifestType);

    @Query(value = """
        SELECT *
        FROM media.media_manifest
        WHERE media_id = :mediaId
          AND processing_generation = :processingGeneration
          AND status = 'READY'
        ORDER BY manifest_type ASC
        """, nativeQuery = true)
    List<MediaManifest> findReadyManifestsByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);

    @Query(value = """
        SELECT *
        FROM media.media_manifest
        WHERE status = :status
        ORDER BY created_at ASC
        """, nativeQuery = true)
    List<MediaManifest> findManifestsByStatus(@Param("status") String status);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_manifest
            WHERE media_id = :mediaId
              AND processing_generation = :processingGeneration
              AND manifest_type = :manifestType
        )
        """, nativeQuery = true)
    boolean existsManifestByMediaIdGenerationAndType(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration,
            @Param("manifestType") String manifestType);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media_manifest
            WHERE media_id = :mediaId
              AND processing_generation = :processingGeneration
              AND status = 'READY'
        )
        """, nativeQuery = true)
    boolean existsReadyManifestByMediaIdAndGeneration(@Param("mediaId") UUID mediaId,
            @Param("processingGeneration") Integer processingGeneration);
}
