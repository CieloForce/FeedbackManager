package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackReceiverService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.infra.aws.AwsSnsConfig;
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
    @Autowired
    public InfoEndpointService(FeedbackReceiverService feedbackReceiverService, SqsService sqsService){
        this.feedbackReceiverService = feedbackReceiverService;
        this.sqsService = sqsService;
    }
    public String getInfo(String queue) {
        if (queue == null) {
            return "Bem-vindo ao Painel Administrativo. Escolha um serviço e forneça os parâmetros necessários.";
        }
        return this.dequeueCustomerFeedbackFromSQS(queue);
    }
    //Mocked info
    public String dequeueCustomerFeedbackFromSQS(String queue){
        //Recover the next published CustomerFeedback from each possible queue from sqs.
        System.out.println("\nObtendo queuUrl da Aws correspondente à fila: " + queue + "\n");
        String queueUrl = sqsService.getQueueUrlByFeedbackType(queue);
        int maxMessages = 1; //Pode ser um outro requestparam... se der tempo!
        //Por default será consumido apenas um item da SQS por vez.
        //mas a opção desse consumo de mensagens ocorrer em batch está habilitada...
        List<Message> messages = feedbackReceiverService.receiveCustomerFeedback(queueUrl,maxMessages);

        StringBuilder result = new StringBuilder();

        for(Message message : messages){
            System.out.println("Raw Message: " + message.toString());
            result.append("Message: ").append(message.body()).append("\n");
            feedbackReceiverService.deleteMessage(queueUrl, message.receiptHandle());
        }
        return result.toString();
    }
}
