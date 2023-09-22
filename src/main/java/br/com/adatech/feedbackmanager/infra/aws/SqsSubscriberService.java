package br.com.adatech.feedbackmanager.infra.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
public class SqsSubscriberService {
    private final SqsClient sqsClient;

    @Autowired
    public SqsSubscriberService(SqsClient sqsClient){
        this.sqsClient = sqsClient;
    }

    //queueUrl é obtido no momento que criamos a fila via código, tipicamente, util se der tempo de automatizar.
    //Aparentemente também é possível fazer o fetch de alguma já criada pelo console.
    public List<Message> consumeMessages(String queueUrl, int maxMessages){
        ReceiveMessageRequest messageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .build();

        ReceiveMessageResponse messageResponse = sqsClient.receiveMessage(messageRequest);

        return messageResponse.messages();
    }

    //receiptHandle é obtido no momento em que você pega o dado da fila sqs.
    // Ele que controla internamente a orquestração do acesso ao dado pelos consumidores.
    public void deleteMessage(String queueUrl, String receiptHandle){
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        sqsClient.deleteMessage(deleteRequest);
    }

    public String getQueueUrlByFeedbackType(String queueType){
        String queueName = "Feedback_" + queueType;
        GetQueueUrlRequest queueUrlRequest = GetQueueUrlRequest.builder().queueName(queueName).build();
        GetQueueUrlResponse queueUrlResponse = sqsClient.getQueueUrl(queueUrlRequest);
        return queueUrlResponse.queueUrl();
    }

}
