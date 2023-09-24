package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.adapter.FeedbackSenderAdapter;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.dao.service.CustomerFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.Topic;

import java.util.List;

@Service
public class SnsPublisherService implements FeedbackSenderAdapter {

    private final SnsClient snsClient;
    private final CustomerFeedbackService repository;

    @Autowired
    public SnsPublisherService(SnsClient snsClient, CustomerFeedbackService repository){
       this.snsClient = snsClient;
       this.repository = repository;
    }

    @Override
    public void sendCustomerFeedback(CustomerFeedback customerFeedback) {
        //Faz a publicação do CustomerFeedback no tópico Sns correspondente ao FeedbackType.
        String messageId = publishCustomerFeedbackToAwsSnsTopic(customerFeedback);
        System.out.println("MessageID retorna pelo publisher: " + messageId);
        customerFeedback.setMessageId(messageId);
        // Fazendo a persistência corretamente com o MessageId do envio para o SNS.
        repository.update(customerFeedback.getUuid(), customerFeedback);
    }

    /** Faz a publicação do CustomerFeedback no tópico SNS da AWS conforme o FeedbackType. **/
    public String publishCustomerFeedbackToAwsSnsTopic(CustomerFeedback customerFeedback){
        String topicARN = null;
        try{
            topicARN = getTopicArnByFeedbackType(customerFeedback.getType());
        }catch (RuntimeException runtimeException){
            System.err.println("Ocorreu um erro ao tentar obter um topicARN da AWS" + runtimeException.getMessage());
        }

        /*
         * O MessageGroupId é um identificador de grupo que ajuda o SNS a GARANTIR internamente a ORDEM DE ENTREGA das
         * mensagens em tópicos FIFO. Se for em tópicos sns do tipo Standard, a ordem não é garantida.
         * Em tópicos SNS do tipo FIFO, cada mensagem com o mesmo MessageGroupID será entregue garantidamente na ordem
         * em que foram enviadas, desde que o atributo interno à nossa aplicação que designamos como MessageGroupId
         * seja suficientemente único. Se esse atributo também não for único o suficiente, podem surgir problemas com
         * a ordem. Por abstração do propósito dessa aplicação, vinculo, portanto, o FeedbackType como sendo
         * o nosso MessageGroupId.
         * */
        String feedbackType = customerFeedback.getType().getDescription();

        System.out.println("\nTopic ARN encontrado: " + topicARN + "\n");

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicARN)
                .messageGroupId(feedbackType)
                .message(customerFeedback.getMessage()).build();

        System.out.println("Request info to publish in SNS topic: " + request.toString());

        PublishResponse response = snsClient.publish(request);

        System.out.println("CustomerFeedback de id " + customerFeedback.getUuid() +
                " foi publicado com sucesso em: " + topicARN);
        return response.messageId();
    }
    /**
     * Faz a busca do ARN do tópico SNS criado na AWS conforme o FeedbackType presente na identificação do ARN.
     * Ao criar o tópico SNS é necessário nomeá-lo de modo a constar o FeedbackType na sua identificação equivalente
     * ao retorno do description para capturá-lo via código poteriormente. Se não houver nenhum tópico equivalente ao FeedbackType
     * fornecido é lançado uma exceção. Para mitigar isso, pode-se restringir estrategicamente o escopo de
     * Feedbacktype no Frontend da aplicação, atribuindo algum tipo como valor padrão. Este método permite
     * fazer corretamente a publicação do CustomerFeedback no tópico SNS correspondente ao seu tipo.
     * **/
    private String getTopicArnByFeedbackType(FeedbackType type) {
        ListTopicsResponse snsTopicsResponse = snsClient.listTopics();
        List<Topic> snsTopics = snsTopicsResponse.topics();
        String feedbackType = type.getDescription();
        System.out.println("Buscando na AWS o topic ARN com base no FeedbackType: " + feedbackType);
        for(Topic topic : snsTopics ) {
            if (topic.topicArn().contains(feedbackType))
                return topic.topicArn();
        }
        //Uma otimazação de desempenho seria usar o factory para atribuir o arn criado ao FeedbackType via setter.
        //Desse modo o FeedbackType já saberia guiar o CustomerFeedback para o tópico SNS correspondente sem precisar fazer o fetch dos tópicos na AWS.
        throw new RuntimeException("ARN do tópico não encontrado para o FeedbackType: " + feedbackType);
        //Logica de criação em caso de inexistência do tópico foi feita no sendCustomerFeedback.
        //Se por algum motivo a criação do tópico lá falhar, essa exceção é lançada.
    }
}
