package br.com.adatech.feedbackmanager.infra.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Service
public class SqsService {
    private final SqsClient sqsClient;

    @Autowired
    public SqsService(SqsClient sqsClient){
        this.sqsClient = sqsClient;
    }

    public String getGeneralQueueSizeInfo(String queueType){
        String queueUrl = this.getQueueUrlByFeedbackType(queueType);
        //listar o nome de todas filas já criadas na AWS. Excluir a fila de messagens mortas.
        //para cada nome, buscar o queueUrl correspondente.

        GetQueueAttributesRequest aprxNumberOfMsgsRrequest = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                .build();

        GetQueueAttributesResponse aprxNumberOfMsgsResponse = sqsClient.getQueueAttributes(aprxNumberOfMsgsRrequest);
        System.out.println(aprxNumberOfMsgsResponse.toString());
        return null;
    }

    public String getQueueName(String targetType){ //funcionando
        ListQueuesResponse response = sqsClient.listQueues();

        for (String queueUrl : response.queueUrls()) {
            String[] parts = queueUrl.split("/");
            String queueName = parts[parts.length - 1];
            if (queueName.contains(targetType)) {
                System.out.println(queueName);
                return queueName;
            }
        }
        return null; // Se não encontrar nenhuma fila
    }

    public String getQueueUrlByFeedbackType(String queueType) { //funcionando
        String queueName = this.getQueueName(queueType); // Faz o fetch do nome da fila automáticamente direto da AWS.
        GetQueueUrlRequest queueUrlRequest = GetQueueUrlRequest.builder().queueName(queueName).build();
        GetQueueUrlResponse queueUrlResponse = sqsClient.getQueueUrl(queueUrlRequest);
        return queueUrlResponse.queueUrl();
    }
}
