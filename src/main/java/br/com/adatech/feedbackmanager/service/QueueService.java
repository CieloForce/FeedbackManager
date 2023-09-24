package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackReceiverService;
import br.com.adatech.feedbackmanager.dao.dto.MessageDTO;
import br.com.adatech.feedbackmanager.dao.service.CustomerFeedbackService;
import br.com.adatech.feedbackmanager.infra.aws.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final SqsService sqsService;
    private final FeedbackReceiverService feedbackReceiverService;

    @Autowired
    public QueueService(FeedbackReceiverService feedbackReceiverService, SqsService sqsService, CustomerFeedbackService repository){
        this.feedbackReceiverService = feedbackReceiverService;
        this.sqsService = sqsService;
    }

    public ResponseEntity<List<MessageDTO>> getMessages(String queue) {
        String queueUrl;
        try {
            queueUrl = sqsService.getQueueUrlByFeedbackType(queue);
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(1)
                    .build();

            ReceiveMessageResponse receiveMessageResponse = sqsService.getSqsClient().receiveMessage(receiveMessageRequest);
            List<Message> messages = receiveMessageResponse.messages();
            List<MessageDTO> messageDtos = messages.stream().map(message -> {
                MessageDTO dto = new MessageDTO();
                dto.setMessageId(message.messageId());
                dto.setReceiptHandle(message.receiptHandle());
                dto.setBody(message.body());
                return dto;
            }).collect(Collectors.toList());

            return new ResponseEntity<>(messageDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> removeMessage(String queue, String receiptHandle) {
        String queueUrl;
        try {
            queueUrl = sqsService.getQueueUrlByFeedbackType(queue);
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(receiptHandle)
                    .build();

            sqsService.getSqsClient().deleteMessage(deleteMessageRequest);
            return new ResponseEntity<>("Message deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
