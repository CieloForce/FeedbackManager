package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.infra.aws.AwsSnsConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class InfoEndpointService {
    public String getInfo(String queue) {
        String message;

        if (queue != null) {
            message = this.getEnqueuedInfoConsumed();
            System.out.println("\nInformações referentes à fila " + queue + "\n");
        } else {
            message = this.getEnqueuedInfoConsumed();
            System.out.println("\n Informações padrão considerando todas as filas \n");
        }

        return message;
    }
    //Mocked info
    public String getEnqueuedInfoConsumed(){
        //TO DO: Discover how to recover the published CustomerFeedback from each possible queue from sqs.

        // Beginning Mock
        ObjectMapper toJSON = new ObjectMapper();
        CustomerFeedback customerFeedbackConsumed = new CustomerFeedback("Sonhe grande, comece pequeno, evolua sempre.", FeedbackType.suggestion);
        String customerFeedbackConsumedJSON = null;
        try{
            customerFeedbackConsumedJSON = toJSON.writeValueAsString(customerFeedbackConsumed);
        } catch(JsonProcessingException e){
            System.out.println("Ocorreu uma exception ao tentar converter objeto CustumerFeedback para JSON: " + e.getMessage() + "\n" + e.getCause());
        }
        // End Mock
        return customerFeedbackConsumedJSON;
    }
}
