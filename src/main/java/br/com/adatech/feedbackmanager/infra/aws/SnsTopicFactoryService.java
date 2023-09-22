package br.com.adatech.feedbackmanager.infra.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;

@Service
public class SnsTopicFactoryService {

    private final SnsClient snsClient;

    @Autowired
    public SnsTopicFactoryService(SnsClient snsClient){
        this.snsClient = snsClient;
    }


    public String createSnsTopicBasedOn(String feedbackType){
        try{
            String topicName = "Feedback_" + feedbackType;
            CreateTopicRequest topicRequest = CreateTopicRequest.builder().name(topicName).build();
            CreateTopicResponse topicResponse = snsClient.createTopic(topicRequest);
            return topicResponse.topicArn();
        }catch(SnsException snsException){
            System.err.println(snsException.awsErrorDetails().errorMessage());
            return "Failure in automatic topic creation";
        }
    }

    public boolean topicExists(String feedbackType) {
        ListTopicsRequest snsExistingTopicsRequest = ListTopicsRequest.builder().build();
        ListTopicsResponse snsExistingTopicsResponse = snsClient.listTopics(snsExistingTopicsRequest);
        List<Topic> snsTopics = snsExistingTopicsResponse.topics();
        return snsTopics.stream().anyMatch((topic) -> topic.topicArn().contains("Feedback_" + feedbackType));
    }
}
