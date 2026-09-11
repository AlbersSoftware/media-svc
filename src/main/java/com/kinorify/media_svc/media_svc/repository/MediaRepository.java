package com.kinorify.media_svc.media_svc.repository;

import com.kinorify.media_svc.media_svc.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id = :mediaId
        """, nativeQuery = true)
    Optional<Media> findMediaById(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id IN (:mediaIds)
        """, nativeQuery = true)
    List<Media> findMediaByIds(@Param("mediaIds") List<UUID> mediaIds);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE uploaded_by_profile_id = :profileId
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findMediaByUploadedByProfileId(@Param("profileId") UUID profileId);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_type = :mediaType
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findMediaByType(@Param("mediaType") String mediaType);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_purpose = :mediaPurpose
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findMediaByPurpose(@Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE status = :status
        ORDER BY created_at ASC
        """, nativeQuery = true)
    List<Media> findMediaByStatus(@Param("status") String status);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id = :mediaId
          AND media_type = :mediaType
        """, nativeQuery = true)
    Optional<Media> findMediaByIdAndType(@Param("mediaId") UUID mediaId,
            @Param("mediaType") String mediaType);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id = :mediaId
          AND status = :status
        """, nativeQuery = true)
    Optional<Media> findMediaByIdAndStatus(@Param("mediaId") UUID mediaId,
            @Param("status") String status);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id = :mediaId
          AND media_type = :mediaType
          AND media_purpose = :mediaPurpose
        """, nativeQuery = true)
    Optional<Media> findMediaByIdTypeAndPurpose(@Param("mediaId") UUID mediaId,
            @Param("mediaType") String mediaType,
            @Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_type = :mediaType
          AND media_purpose = :mediaPurpose
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findMediaByTypeAndPurpose(@Param("mediaType") String mediaType,
            @Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE uploaded_by_profile_id = :profileId
          AND media_purpose = :mediaPurpose
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findMediaByProfileIdAndPurpose(@Param("profileId") UUID profileId,
            @Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE uploaded_by_profile_id = :profileId
          AND media_type = :mediaType
          AND media_purpose = :mediaPurpose
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findMediaByProfileIdTypeAndPurpose(@Param("profileId") UUID profileId,
            @Param("mediaType") String mediaType,
            @Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_type = 'VIDEO'
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Media> findVideos();

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_type = 'VIDEO'
          AND status = :status
        ORDER BY created_at ASC
        """, nativeQuery = true)
    List<Media> findVideosByStatus(@Param("status") String status);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id = :mediaId
          AND media_type = 'VIDEO'
        """, nativeQuery = true)
    Optional<Media> findVideoById(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id IN (:mediaIds)
          AND status = 'READY'
        """, nativeQuery = true)
    List<Media> findReadyMediaByIds(@Param("mediaIds") List<UUID> mediaIds);

    @Query(value = """
        SELECT *
        FROM media.media
        WHERE media_id IN (:mediaIds)
          AND media_purpose = :mediaPurpose
          AND status = 'READY'
        """, nativeQuery = true)
    List<Media> findReadyMediaByIdsAndPurpose(@Param("mediaIds") List<UUID> mediaIds,
            @Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media
            WHERE media_id = :mediaId
        )
        """, nativeQuery = true)
    boolean existsMediaById(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media
            WHERE media_id = :mediaId
              AND media_purpose = :mediaPurpose
        )
        """, nativeQuery = true)
    boolean existsMediaByIdAndPurpose(@Param("mediaId") UUID mediaId,
            @Param("mediaPurpose") String mediaPurpose);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media
            WHERE media_id = :mediaId
              AND status = 'READY'
        )
        """, nativeQuery = true)
    boolean existsReadyMediaById(@Param("mediaId") UUID mediaId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM media.media
            WHERE media_id = :mediaId
              AND media_type = 'VIDEO'
        )
        """, nativeQuery = true)
    boolean existsVideoById(@Param("mediaId") UUID mediaId);
}
