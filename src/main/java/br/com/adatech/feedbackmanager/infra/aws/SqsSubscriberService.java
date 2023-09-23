package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.adapter.FeedbackReceiverAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
public class SqsSubscriberService implements FeedbackReceiverAdapter {
    private final SqsClient sqsClient;

    @Autowired
    public SqsSubscriberService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public List<Message> receiveCustomerFeedback(String queueUrl, int maxMessages) {
        ReceiveMessageRequest messageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .build();

        ReceiveMessageResponse messageResponse = sqsClient.receiveMessage(messageRequest);

        return messageResponse.messages();
    }

    //receiptHandle é obtido no momento em que você pega o dado da fila sqs.
    // Ele que controla internamente a orquestração do acesso distribuído ao dado pelos consumidores.
    /** Faz a deleção da mensagem consumida na fila sqs da AWS orquestrando o acesso ao recurso por mais de um consumidor em paralelo.**/
    @Override
    public void deleteMessage(String queueUrl, String receiptHandle) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        sqsClient.deleteMessage(deleteRequest);
    }

}