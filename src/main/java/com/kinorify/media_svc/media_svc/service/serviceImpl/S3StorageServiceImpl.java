package com.kinorify.media_svc.media_svc.service.serviceImpl;

import com.kinorify.media_svc.media_svc.dto.S3ObjectMetadataDTO;
import com.kinorify.media_svc.media_svc.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    // add jwt later

    @Override
    public String generatePresignedUploadUrl(String storageKey, String contentType, Duration duration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(duration)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    @Override
    public String generatePresignedViewUrl(String storageKey, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    @Override
    public S3ObjectMetadataDTO getObjectMetadata(String storageKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build();

        try {
            HeadObjectResponse response = s3Client.headObject(request);

            return new S3ObjectMetadataDTO(
                    storageKey,
                    response.contentLength(),
                    response.contentType(),
                    response.eTag()
            );
        } catch (NoSuchKeyException e) {
            throw new IllegalStateException(
                    "S3 object not found: " + storageKey,
                    e
            );
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new IllegalStateException(
                        "S3 object not found: " + storageKey,
                        e
                );
            }

            throw new IllegalStateException(
                    "Unable to read S3 object metadata: " + storageKey,
                    e
            );
        } catch (SdkException e) {
            throw new IllegalStateException(
                    "Unable to communicate with S3.",
                    e
            );
        }
    }

    @Override
    public boolean objectExists(String storageKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build();

        try {
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }

            throw new IllegalStateException(
                    "Unable to determine whether S3 object exists: " + storageKey,
                    e
            );
        } catch (SdkException e) {
            throw new IllegalStateException(
                    "Unable to communicate with S3.",
                    e
            );
        }
    }

    @Override
    public void deleteObject(String storageKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (SdkException e) {
            throw new IllegalStateException(
                    "Unable to delete S3 object: " + storageKey,
                    e
            );
        }
    }

    @Override
    public List<String> listObjectKeys(String storagePrefix) {

        ListObjectsV2Request request =
                ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(storagePrefix)
                        .build();

        ListObjectsV2Response response =
                s3Client.listObjectsV2(
                        request
                );

        return response.contents()
                .stream()
                .map(S3Object::key)
                .toList();
    }
}
