package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.adapter.FeedbackSenderAdapter;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;

@Service
public class SnsFeedbackSender implements FeedbackSenderAdapter {

    private final SnsClient snsClient;

    @Autowired
    public SnsFeedbackSender(SnsClient snsClient){
       this.snsClient = snsClient;
    }
    @Override
    public void sendCustomerFeedback(CustomerFeedback customerFeedback) {
        //TO DO: fazer a publicação do CustomerFeedback no tópico da AWS accordingly with its type.
        System.out.println("OK!!");
    }
}
