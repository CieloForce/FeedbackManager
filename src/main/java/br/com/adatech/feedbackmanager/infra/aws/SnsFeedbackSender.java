package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.adapter.FeedbackSenderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsFeedbackSender implements FeedbackSenderAdapter {

    private final AmazonSimpleNotificationService amazonSimpleNotificationService;

    @Autowired
    public SnsFeedbackSender(AmazonSimpleNotificationService amazonSimpleNotificationService){
        this.amazonSimpleNotificationService = amazonSimpleNotificationService;
    }

    //Override interface method

}
