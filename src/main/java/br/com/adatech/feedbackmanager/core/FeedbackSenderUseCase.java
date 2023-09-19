package br.com.adatech.feedbackmanager.core;

import br.com.adatech.feedbackmanager.entity.CustomerFeedback;
import org.springframework.stereotype.Service;

/** O que a aplicação faz - casos de uso - regras de negócio **/
@Service
public interface FeedbackSenderUseCase {

    /**
     * Recebe como parâmetro CustomerFeedback que serão enfileirados e processados
     * conforme a ordem em que foram recebidos.
     * **/
    void sendCustomerFeedback(CustomerFeedback customerFeedback);


}