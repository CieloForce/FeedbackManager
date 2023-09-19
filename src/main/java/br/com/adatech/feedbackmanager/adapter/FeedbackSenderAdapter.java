package br.com.adatech.feedbackmanager.adapter;

import br.com.adatech.feedbackmanager.entity.CustomerFeedback;

public interface FeedbackSenderAdapter {
    void sendCustomerFeedback(CustomerFeedback customerFeedback);
}
