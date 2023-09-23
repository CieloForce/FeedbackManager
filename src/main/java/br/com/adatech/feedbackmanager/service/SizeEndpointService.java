package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.infra.aws.SqsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.stereotype.Service;

@Service
public class SizeEndpointService {

    private final SqsService sqsService;

    @Autowired
    public SizeEndpointService(SqsService sqsService){
        this.sqsService = sqsService;
    }

    public String getSize(String queue) {
        String message;

        if (queue != null) {
            message =  this.queueSize();
            System.out.println("\nTamanho referente à fila " + queue + "\n");

            String queueUrl = sqsService.getQueueUrlByFeedbackType(queue); //teste
            System.out.println(queueUrl);//teste
            sqsService.getGeneralQueueSizeInfo(queue);


        } else {
            message = this.queueSize();
            System.out.println("\nTamanho padrão considerando todas as filas juntas\n");
        }


        message = "{ \"topics\": { \"elogio\": { \"ApproximateNumberOfMessages\": 0, \"ApproximateNumberOfMessagesNotVisible\": 0, \"ApproximateNumberOfMessagesDelayed\": 0, \"totalSize\": 0 }, \"sugestao\": { \"ApproximateNumberOfMessages\": 0, \"ApproximateNumberOfMessagesNotVisible\": 0, \"ApproximateNumberOfMessagesDelayed\": 0, \"totalSize\": 0 }, \"critica\": { \"ApproximateNumberOfMessages\": 0, \"ApproximateNumberOfMessagesNotVisible\": 0, \"ApproximateNumberOfMessagesDelayed\": 0, \"totalSize\": 0 } }, \"totalSize\": 0 }";

        return message;
    }

    //Mock
    public String queueSize(){
        //TO DO: Dicover how to get size of each queue from sqs.

        //Mock
        String queueSize = "queueSize: 997";
        ObjectMapper toJSON = new ObjectMapper();
        String queueSizeJSON = null;
        try{
            queueSizeJSON = toJSON.writeValueAsString(queueSize);
        } catch(JsonProcessingException e){
            System.out.println("Ocorreu uma exception ao tentar converter objeto queueSize para JSON: " + e.getMessage() + "\n" + e.getCause());
        }
        // End Mock
        return queueSizeJSON;
    }
}
