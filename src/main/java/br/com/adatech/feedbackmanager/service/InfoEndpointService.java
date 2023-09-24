package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackReceiverService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackStatus;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.core.util.FeedbackTypeConverter;
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

        CustomerFeedback result = dequeueCustomerFeedbackFromSQS(queue);
        if(result != null)
            return fromObectToJSON(result);
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
        String messagePayload = message.body();
        //deleta a mensagem da fila sqs considerando o acesso distribuído entre consumidores, representado pelo mesmo endpoint.
        feedbackReceiverService.deleteMessage(queueUrl, message.receiptHandle());
        System.out.println("ID da mensagem consumida: " + messageId);
       // return fromObectToJSON(messageId); //Até aqui OK. Retirar persistência se problema continuar.

        // Problema na persistencia mapeado.
        CustomerFeedback customerFeedback = null;
        try {
            System.out.println("Tentando buscar registro no banco de dados depois do consumo...");
            //Recupera CustomerFeedback do banco de dados
            customerFeedback = repository.findById(messageId);
            System.out.println("Registro encontrado: " + customerFeedback.toString() );
            //Muda o status para finalizado
            customerFeedback.setStatus(FeedbackStatus.finished);
            //Salva a atualização no banco de dados
            repository.update(customerFeedback.getUuid(), customerFeedback);
        } catch (Exception e) {
            System.err.println("Ocorreu um erro ao processar CustomerFeedback consumido no banco de dados: " + e.getMessage());
            //Retornando o MessageID consumido
            System.out.println("Retornando o que foi consumido ao invés do objeto real do banco de dados para: " + queue);
            CustomerFeedback consumedCustomerFeedback = new CustomerFeedback(messagePayload, messageId, FeedbackTypeConverter.fromString(queue));
            consumedCustomerFeedback.setStatus(FeedbackStatus.finished);
            //Retorna o objeto consumido de acordo com a Aws.
            return consumedCustomerFeedback;
        }
        //Retorna o objeto consumido corretamente de acordo com o banco de dados.
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
