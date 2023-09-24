package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackReceiverService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackStatus;
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

    private boolean nothingToConsume = false;
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

        if(dequeueCustomerFeedbackFromSQS(queue) != null)
            return fromObectToJSON(dequeueCustomerFeedbackFromSQS(queue));
        else if(nothingToConsume){
            return "Nothing to consume!";
        }
        else return "Keep sending, consuming and checking size info!";
    }

    /** Recover (consumes) the next published CustomerFeedback from aws sqs FIFO queue, in the arrived order. **/
    public CustomerFeedback dequeueCustomerFeedbackFromSQS(String queue) {
        System.out.println("\nObtendo queuUrl da Aws correspondente à fila: " + queue + "\n");

        String queueUrl;
        try{
            queueUrl = sqsService.getQueueUrlByFeedbackType(queue);
        }catch(RuntimeException e){
            System.err.println("Ocorreu um erro ao processar o tipo da fila informado por parâmetro da url." +
                    "Verifique se a fila existe ou confira sua ortografia correta.");
            return null;
        }

        int maxMessages = 1; //allowing batch queue consumption in order.
        List<Message> messages = feedbackReceiverService.receiveCustomerFeedback(queueUrl, maxMessages);

        if (messages.isEmpty()) {
            setNothingToConsume(true);
            System.out.println("Nothing to consume!");
            return null;
        }

        Message message = messages.get(0);
        String messageId = message.messageId();
        //deleta a mensagem da fila sqs considerando o acesso distribuído entre consumidores, representado pelo mesmo endpoint.
        feedbackReceiverService.deleteMessage(queueUrl, message.receiptHandle());
        System.out.println("ID da mensagem consumida: " + messageId);
        CustomerFeedback customerFeedback = null;
        try {
            //Recupera CustomerFeedback do banco de dados
            customerFeedback = repository.findById(messageId);
            //Muda o status para finalizado
            customerFeedback.setStatus(FeedbackStatus.finished);
            //Salva a atualização no banco de dados
            repository.update(customerFeedback.getUuid(), customerFeedback);
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao processar CustomerFeedback consumido no banco de dados: " + e.getMessage());
        }
        //Retorna o objeto consumido corretamente
        return customerFeedback;
    }
    public String fromObectToJSON(Object object){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }catch(JsonProcessingException e){
            System.err.println("Ocorreu um erro ao converter o objeto para JSON: " + e.getMessage());
            return null;
        }
    }

    public void setNothingToConsume(boolean nothingToConsume) {
        this.nothingToConsume = nothingToConsume;
    }
}
