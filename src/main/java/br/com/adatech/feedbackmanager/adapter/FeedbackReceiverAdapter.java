package br.com.adatech.feedbackmanager.adapter;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public interface FeedbackReceiverAdapter {
    List<Message> receiveCustomerFeedback(String queueUrl, int maxMessages);
    void deleteMessage(String queueUrl, String receiptHandle);
}
