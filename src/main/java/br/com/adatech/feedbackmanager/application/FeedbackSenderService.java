package br.com.adatech.feedbackmanager.application;

import br.com.adatech.feedbackmanager.adapter.FeedbackSenderAdapter;
import br.com.adatech.feedbackmanager.core.UseCase.FeedbackSenderUseCase;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
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