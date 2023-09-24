package br.com.adatech.feedbackmanager.infra.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SnsTopicFactoryService {
    private final SnsClient snsClient;

    @Autowired
    public SnsTopicFactoryService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String createFifoTopic(String feedbackType) {
        String topicName = "Automation_Feedback_" + feedbackType + ".fifo";
        Map<String, String> configs = new HashMap<>();
        configs.put("FifoTopic", "true");
        configs.put("ContentBasedDeduplication","true");

        CreateTopicRequest topicRequest = CreateTopicRequest.builder()
                .name(topicName)
                .attributes(configs)
                .build();

        CreateTopicResponse topicResponse = snsClient.createTopic(topicRequest);
        return topicResponse.topicArn();
    }

    public boolean topicExists(String feedbackType) {
        ListTopicsRequest snsExistingTopicsRequest = ListTopicsRequest.builder().build();
        ListTopicsResponse snsExistingTopicsResponse = snsClient.listTopics(snsExistingTopicsRequest);
        List<Topic> snsTopics = snsExistingTopicsResponse.topics();
        return snsTopics.stream().anyMatch((topic) -> topic.topicArn().contains("Feedback_" + feedbackType));
    }

    public void addPermissionToTopic(String topicArn) {
        String policyJson = "{" +
                "  \"Version\": \"2012-10-17\"," +
                "  \"Statement\": [" +
                "    {" +
                "      \"Effect\": \"Allow\"," +
                "      \"Principal\": \"*\"," +
                "      \"Action\": [" +
                "        \"sns:Publish\"," +
                "        \"sns:Subscribe\"" +
                "      ]," +
                "      \"Resource\": \"" + topicArn + "\"" +
                "    }" +
                "  ]" +
                "}";

        SetTopicAttributesRequest setTopicAttributesRequest = SetTopicAttributesRequest.builder()
                .topicArn(topicArn)
                .attributeName("Policy")
                .attributeValue(policyJson)
                .build();

        snsClient.setTopicAttributes(setTopicAttributesRequest);
    }
}
