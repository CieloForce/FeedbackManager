package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackSenderService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackStatus;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.core.util.FeedbackTypeConverter;
import br.com.adatech.feedbackmanager.dao.dto.CustomerFeedbackDTO;
import br.com.adatech.feedbackmanager.dao.service.CustomerFeedbackService;
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

        // Casos de uso
        try {
            //Criar tópicos SNS na AWS via código se der tempo. Feito, mas não testado.
            //Criar filas SQS correspondentes a cada tópico via código se der tempo. A fazer.
            //Fazer cada fila SQS criada assinar o seu tópico SNS correspondente via código se der tempo. A fazer.

            //Publica CustomerFeedback nos tópicos SNS conforme o FeedbackType
            this.feedbackSenderService.sendCustomerFeedback(customerFeedback);

            //Atualiza o status para "em processamento" e atualiza o valor no banco de dados.
            customerFeedback.setStatus(FeedbackStatus.processing);
            repository.update(customerFeedback.getUuid(), customerFeedback);

            return ResponseEntity.ok("Customer Feedback published to SNS sucessfully!");
        }catch (Exception e) {
            // TO DO: Discover the "real" exception type triggered here and personalize it properly.
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in send customer feedback to SNS topic");
        }
    }

}
