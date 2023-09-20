package br.com.adatech.feedbackmanager.infra.aws;

import br.com.adatech.feedbackmanager.adapter.FeedbackSenderAdapter;
import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import com.amazonaws.services.sns.AmazonSNS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnsFeedbackSender implements FeedbackSenderAdapter {

    private final AmazonSNS amazonSNS;

    @Autowired
    public SnsFeedbackSender(AmazonSNS amazonSNS){
        this.amazonSNS = amazonSNS;
    }
    @Override
    public void sendCustomerFeedback(CustomerFeedback customerFeedback) {
        //TO DO: fazer a publicação do CustomerFeedback no tópico da AWS accordingly with its type.
    }
}
