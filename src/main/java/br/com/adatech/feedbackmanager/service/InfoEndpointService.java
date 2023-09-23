package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackReceiverService;
import br.com.adatech.feedbackmanager.dao.service.CustomerFeedbackService;
import br.com.adatech.feedbackmanager.infra.aws.SqsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
public class InfoEndpointService {
    private final FeedbackReceiverService feedbackReceiverService;
    private final SqsService sqsService;

    private final CustomerFeedbackService repository;
    @Autowired
    public InfoEndpointService(FeedbackReceiverService feedbackReceiverService, SqsService sqsService, CustomerFeedbackService repository){
        this.feedbackReceiverService = feedbackReceiverService;
        this.sqsService = sqsService;
        this.repository = repository;
    }
    public String getInfo(String queue) {
        if (queue == null) {
            return "Bem-vindo ao Painel Administrativo. Envie seu feedback!";
        }
        return fromStringToJSON(dequeueCustomerFeedbackFromSQS(queue));
    }

    /** Recover (consumes) the next published CustomerFeedback from aws sqs FIFO queue, in the arrived order. **/
    public String dequeueCustomerFeedbackFromSQS(String queue){
        System.out.println("\nObtendo queuUrl da Aws correspondente à fila: " + queue + "\n");
        String queueUrl = sqsService.getQueueUrlByFeedbackType(queue);
        int maxMessages = 1; //allowing batch queue consumption in order.
        List<Message> messages = feedbackReceiverService.receiveCustomerFeedback(queueUrl,maxMessages);

        StringBuilder result = new StringBuilder();

        for(Message message : messages){
            System.out.println("\nRaw Message: " + message.toString() + "\n");
            result.append("Message: ").append(message.body()).append("\n");
            feedbackReceiverService.deleteMessage(queueUrl, message.receiptHandle());
        }

        return result.toString();
    }

    public String fromStringToJSON(String string){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(string);
        }catch(JsonProcessingException e){
            System.err.println("Ocorreu um erro ao converter as informações gerais sobre os tamanhos das filas: " + e.getMessage());
            return null;
        }
    }
}
