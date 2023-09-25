package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackReceiverService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackStatus;
import br.com.adatech.feedbackmanager.core.util.FeedbackTypeConverter;
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
    private final CustomerFeedbackService repository;
    private final InfoEndpointService infoEndpointService;

    @Autowired
    public QueueService(FeedbackReceiverService feedbackReceiverService, SqsService sqsService,
                        InfoEndpointService infoEndpointService,
                        CustomerFeedbackService repository){
        this.repository = repository;
        this.sqsService = sqsService;
        this.infoEndpointService = infoEndpointService;
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
            MessageDTO payload = null;
            List<MessageDTO> messageDtos = messages.stream().map(message -> {
                MessageDTO dto = new MessageDTO();
                dto.setMessageId(message.messageId());
                dto.setReceiptHandle(message.receiptHandle());
                dto.setBody(message.body());

                String desiredMessageID = infoEndpointService.getFieldFromJson(dto.getBody(), "MessageId");
                CustomerFeedback customerFeedback = null;
                try {
                    System.out.println("Tentando buscar registro no banco de dados depois do consumo...");
                    //Recupera CustomerFeedback do banco de dados
                    customerFeedback = repository.findFeedbackByMessageId(desiredMessageID); //Passando o REAL messageID do objeto!
                    System.out.println("Registro encontrado: " + customerFeedback.toString() );
                    //Muda o status para finalizado
                    customerFeedback.setStatus(FeedbackStatus.finished);
                    //Salva a atualização no banco de dados
                    repository.update(customerFeedback.getUuid(), customerFeedback);


                } catch (Exception e) {
                    System.out.println("Não foi possível recuperar CustomerFeedback consumido no banco de dados: " + e.getMessage());
                    //Retornando o MessageID consumido
                    System.out.println("Retornando o que foi consumido na fila SQS ao invés do objeto real do banco de dados para: " + queue);
                    CustomerFeedback consumedCustomerFeedback = new CustomerFeedback(dto.getBody(), desiredMessageID, FeedbackTypeConverter.fromString(queue));
                    consumedCustomerFeedback.setStatus(FeedbackStatus.finished);
                    //Retorna o objeto consumido de acordo com a Aws.
                    //return consumedCustomerFeedback;
                }
                //Retorna o objeto consumido corretamente de acordo com o banco de dados.
               // return customerFeedback;

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
