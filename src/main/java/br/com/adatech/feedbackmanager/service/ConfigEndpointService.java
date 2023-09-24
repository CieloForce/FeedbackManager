package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.dao.dto.CustomerFeedbackDTO;
import br.com.adatech.feedbackmanager.infra.aws.SnsTopicFactoryService;
import br.com.adatech.feedbackmanager.infra.aws.SqsQueueFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigEndpointService {

    private final SnsTopicFactoryService topicFactoryService;
    private final SqsQueueFactoryService queueFactoryService;

    @Autowired
    public ConfigEndpointService(SnsTopicFactoryService topicFactoryService, SqsQueueFactoryService queueFactoryService){
        this.topicFactoryService = topicFactoryService;
        this.queueFactoryService = queueFactoryService;
    }

    public String config(){

        System.out.println("Criando tópicos SNS FIFO na aws via automação por API...");
        String topicARNCriticism = null;
        String topicARNCompliment = null;
        String topicARNSuggestion = null;

        topicARNCriticism = topicFactoryService.createFifoTopic(FeedbackType.CRITICISM.getDescription());
        topicARNCompliment = topicFactoryService.createFifoTopic(FeedbackType.COMPLIMENT.getDescription());
        topicARNSuggestion = topicFactoryService.createFifoTopic(FeedbackType.SUGGESTION.getDescription());

        System.out.println("Criando Filas SQS FIFO...");

        String queueUrlCriticism = null;
        String queueUrlCompliment = null;
        String queueUrlSuggestion = null;
        queueUrlCriticism = queueFactoryService.createQueuesFIFO(FeedbackType.CRITICISM.getDescription());
        queueUrlCompliment = queueFactoryService.createQueuesFIFO(FeedbackType.COMPLIMENT.getDescription());
        queueUrlSuggestion = queueFactoryService.createQueuesFIFO(FeedbackType.SUGGESTION.getDescription());

        return "{Message: Configure manualmente as subscrições SNS/SQS para os tópicos e filas criados.}";
    }
}
