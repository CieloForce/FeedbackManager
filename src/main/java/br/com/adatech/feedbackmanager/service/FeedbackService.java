package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.application.FeedbackSenderService;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.dao.service.CustomerFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/** Abstrai as regras de negócio da camada de controller deixando-a mais enxuta **/
@Service
public class FeedbackService {
    /** Faz a publicação do CustomerFeedback no tópico **/
    private final FeedbackSenderService feedbackSenderService;

    /** Faz a persistência do CustomerFeedback no repositório da aplicação **/
    private final CustomerFeedbackService repository;

    @Autowired
    public FeedbackService(FeedbackSenderService feedbackSenderService, CustomerFeedbackService repository){
        this.feedbackSenderService = feedbackSenderService;
        this.repository = repository;
    }

    public ResponseEntity<String> sendFeedback(CustomerFeedback customerFeedback){
        try {
            //Criar tópicos SNS na AWS.
            //Criar filas SQS correspondentes a cada tópico.
            //Fazer cada fila SQS criada assinar o seu tópico SNS correspondente.

            //Abastecer os tópicos SNS com CustomerFeedback conforme seu tipo
            this.feedbackSenderService.sendCustomerFeedback(customerFeedback);

            return ResponseEntity.ok("Customer Feedback published sucessfully");
        }catch (Exception e) {
            // TO DO: Discover the "real" exception type triggered here and personalize it properly.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in send customer feedback to topic SNS");
        }
    }

    public int queueSize(){
        //TO DO: Dicover how to get size of each queue from sqs.
        return 0;
    }

    public CustomerFeedback[] getInfo(){
        //TO DO: Discover how to recover the published CustomerFeedback from each possible queue from sqs.
        return null;
    }
}
