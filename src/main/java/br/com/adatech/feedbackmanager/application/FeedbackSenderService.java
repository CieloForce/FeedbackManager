package br.com.adatech.feedbackmanager.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackSenderService implements FeedbackSenderUseCase {

    private final FeedbackSenderAdapter feedbackSenderAdapter;

    @Autowired
    public FeedbackSenderService(FeedbackSenderAdapter feedbackSenderAdapter){
        this.feedbackSenderAdapter = feedbackSenderAdapter;
    }

    @Override
    public void sendCustomerFeedback(CustomerFeedback customerFeedback) {
        this.feedbackSenderAdapter.sendCustomerFeedback(customerFeedback);
    }
}