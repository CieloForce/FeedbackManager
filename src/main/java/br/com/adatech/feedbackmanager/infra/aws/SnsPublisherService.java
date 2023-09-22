package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.adapter.FeedbackSenderAdapter;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.Topic;

import java.util.List;

@Service
public class SnsPublisherService implements FeedbackSenderAdapter {

    private final SnsClient snsClient;

    @Autowired
    public SnsPublisherService(SnsClient snsClient){
       this.snsClient = snsClient;
    }

    @Override
    public void sendCustomerFeedback(CustomerFeedback customerFeedback) {
        publishCustomerFeedbackToAwsSns(customerFeedback);
        System.out.println("OK!!");
    }

    /** Faz a publicação do CustomerFeedback no tópico SNS da AWS conforme o FeedbackType. **/
    public void publishCustomerFeedbackToAwsSns(CustomerFeedback customerFeedback){
        String topicARN = getTopicArnByFeedbackType(customerFeedback.getType());

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicARN)
                .message(customerFeedback.getMessage()).build();

        snsClient.publish(request);
    }
    /**
     * Faz a busca do ARN do tópico SNS criado na AWS conforme o FeedbackType presente na identificação do ARN.
     * Ao criar o tópico SNS é necessário nomeá-lo de modo a constar o FeedbackType na sua identificação
     * para capturá-lo via código poteriormente. Se não houver nenhum tópico equivalente ao FeedbackType
     * fornecido é lançado uma exceção. Para mitigar isso, pode-se restringir estrategicamente o escopo de
     * Feedbacktype no Frontend da aplicação, atribuindo algum tipo como valor padrão.
     * **/
    private String getTopicArnByFeedbackType(FeedbackType type) {
        ListTopicsResponse snsTopicsResponse = snsClient.listTopics();
        List<Topic> snsTopics = snsTopicsResponse.topics();
        String feedbackType = type.getDescription();

        for(Topic topic : snsTopics ) {
            if (topic.topicArn().contains(feedbackType))
                return topic.topicArn();
        }

        throw new RuntimeException("ARN do tópico não encontrado para o FeedbackType: " + feedbackType);
    }

}
