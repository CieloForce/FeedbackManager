package br.com.adatech.feedbackmanager.adapter;


import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;

public interface FeedbackSenderAdapter {
    void sendCustomerFeedback(CustomerFeedback customerFeedback);
}
