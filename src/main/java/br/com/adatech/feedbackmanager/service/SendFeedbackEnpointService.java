package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackSenderService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackStatus;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.core.util.FeedbackTypeConverter;
import br.com.adatech.feedbackmanager.dao.dto.CustomerFeedbackDTO;
import br.com.adatech.feedbackmanager.dao.service.CustomerFeedbackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/** Abstrai as regras de negócio da camada de controller deixando-a mais enxuta **/
@Service
public class SendFeedbackEnpointService {
    /** Faz a publicação do CustomerFeedback no tópico **/
    private final FeedbackSenderService feedbackSenderService;

    /** Faz a persistência do CustomerFeedback no repositório da aplicação **/
    private final CustomerFeedbackService repository;

    @Autowired
    public SendFeedbackEnpointService(FeedbackSenderService feedbackSenderService, CustomerFeedbackService repository){
        this.feedbackSenderService = feedbackSenderService;
        this.repository = repository;
    }

    public ResponseEntity<String> sendFeedback(CustomerFeedbackDTO customerFeedbackDTO){
        //Construir o model CustomerFeedback a partir do DTO.
        FeedbackType type = FeedbackTypeConverter.fromString(customerFeedbackDTO.type());
        String message = customerFeedbackDTO.message();
        CustomerFeedback customerFeedback = new CustomerFeedback(message, type);
        //Fazer a persistência no banco.
        repository.create(customerFeedback);
        try {
            //Publica CustomerFeedback nos tópicos SNS conforme o FeedbackType
            this.feedbackSenderService.sendCustomerFeedback(customerFeedback);

            System.out.println("Check customerFeedback no sender: " + customerFeedback.toString());

            //Atualiza o status do CustomerFeedback para "em processamento" e atualiza o valor dele no banco de dados.
            customerFeedback.setStatus(FeedbackStatus.processing);
            repository.update(customerFeedback.getUuid(), customerFeedback);
            //método update atualizado para setar MessageId: camada de persistência corrigida

            System.out.println("Check customerFeedback no sender depois do update: " + customerFeedback.toString());
            // o estado se mantém.


            return ResponseEntity.ok(customerFeedbackToJSON(customerFeedback));
        }catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in send customer feedback to SNS topic");
        }
    }

    public String customerFeedbackToJSON(CustomerFeedback customerFeedback){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(customerFeedback);
        }catch(JsonProcessingException e){
            System.err.println("Ocorreu um erro ao converter CustomerFeedback para Json: " + e.getMessage());
            return null;
        }
    }

}
