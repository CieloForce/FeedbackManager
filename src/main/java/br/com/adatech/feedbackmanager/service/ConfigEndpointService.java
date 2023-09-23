package br.com.adatech.feedbackmanager.service;

import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import br.com.adatech.feedbackmanager.infra.aws.SnsTopicFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigEndpointService {
//
//    private SnsTopicFactoryService topicFactoryService;
//
//    @Autowired
//    public ConfigEndpointService(SnsTopicFactoryService topicFactoryService){
//        this.topicFactoryService = topicFactoryService;
//    }
//
//    public String config(){
//        //Criar os tópicos fifo sns.
//        //Cirar as filas fifo sqs
//        //Fazer as filas sqs assinarem seus respectivos tópicos sns
//        //Conferir automação.
//
//
//        //Se na AWS não existe ainda um tópico SNS configurado com base no CustomerFeedback fornecido,
//        // crie um e o vincule à fila sqs fifo automaticamente.
//        if(!(topicFactoryService.topicExists(FeedbackType.CRITISCISM.toString()))
//            topicFactoryService.createSnsTopicBasedOn(customerFeedback.getType().getDescription());
//
//
//        return "Ready to use!";
//    }
}
