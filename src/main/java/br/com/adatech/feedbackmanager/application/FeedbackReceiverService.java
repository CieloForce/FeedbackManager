package br.com.adatech.feedbackmanager.application;

import br.com.adatech.feedbackmanager.adapter.FeedbackReceiverAdapter;
import br.com.adatech.feedbackmanager.core.UseCase.FeedbackReceiverUseCase;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
public class FeedbackReceiverService implements FeedbackReceiverUseCase {

    private final FeedbackReceiverAdapter feedbackReceiverAdapter;

    @Autowired
    public FeedbackReceiverService(FeedbackReceiverAdapter feedbackReceiverAdapter){
        this.feedbackReceiverAdapter = feedbackReceiverAdapter;
    }

    @Override
    public List<Message> receiveCustomerFeedback(String queueUrl, int maxMessages) {
        return this.feedbackReceiverAdapter.receiveCustomerFeedback(queueUrl, maxMessages);
    }

    @Override
    public void deleteMessage(String queueUrl, String receiptHandle) {
        this.feedbackReceiverAdapter.deleteMessage(queueUrl, receiptHandle);
    }
}
