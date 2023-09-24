package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.core.entity.FeedbackType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SqsQueueFactoryService {
    private final SqsClient sqsClient;
    private final SnsClient snsClient;
    @Autowired
    public SqsQueueFactoryService(SqsClient sqsClient, SnsClient snsClient){
        this.sqsClient = sqsClient;
        this.snsClient = snsClient;
    }

    public String createQueuesFIFO(String feedbackType) {
        String queueName = "Automation_Feedback_" + feedbackType + ".fifo";

        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.FIFO_QUEUE, "true");
        attributes.put(QueueAttributeName.CONTENT_BASED_DEDUPLICATION, "true");

        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .attributes(attributes)
                .build();

        try {
            CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);

            GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();
            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest);

            return getQueueUrlResponse.queueUrl();
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return "Failed to create FIFO queue for feedback type " + feedbackType;
        }
    }

    public boolean doesQueueExist(String feedbackType) {
        ListQueuesRequest request = ListQueuesRequest.builder()
                .queueNamePrefix("Automation_Feedback_" + feedbackType + ".fifo")
                .build();

        ListQueuesResponse response = sqsClient.listQueues(request);

        for (String url : response.queueUrls()) {
            String[] parts = url.split("/");
            String existingQueueName = parts[parts.length - 1];
            if (existingQueueName.equals(feedbackType)) {
                return true;
            }
        }

        return false;
    }
    public void subscribeQueueToTopic(String queueArn, String topicArn) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("SQS")
                .endpoint(queueArn)
                .topicArn(topicArn)
                .build();

        snsClient.subscribe(request);
    }

    public String getQueueArn(String queueUrl) {
        GetQueueAttributesResponse response = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .build());

        return response.attributes().get(QueueAttributeName.QUEUE_ARN);
    }

}
