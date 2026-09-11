package com.kinorify.media_svc.media_svc.service.serviceImpl;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.kinorify.media_svc.media_svc.dto.request.VideoProcessingJobRequestDTO;
import com.kinorify.media_svc.media_svc.service.VideoProcessingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
@RequiredArgsConstructor
public class VideoProcessingQueueServiceImpl
        implements VideoProcessingQueueService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.video-processing-queue-url}")
    private String videoProcessingQueueUrl;

    @Override
    public void sendProcessingJob(VideoProcessingJobRequestDTO request) {

        String messageBody;

        try {
            messageBody = objectMapper.writeValueAsString(request);
        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "Unable to serialize video processing job.",
                    e
            );
        }

        SendMessageRequest sendMessageRequest =
                SendMessageRequest.builder()
                        .queueUrl(videoProcessingQueueUrl)
                        .messageBody(messageBody)
                        .build();

        try {
            sqsClient.sendMessage(sendMessageRequest);
        } catch (SqsException e) {
            throw new IllegalStateException(
                    "Unable to send video processing job to SQS.",
                    e
            );
        } catch (SdkException e) {
            throw new IllegalStateException(
                    "Unable to communicate with SQS.",
                    e
            );
        }
    }
}
