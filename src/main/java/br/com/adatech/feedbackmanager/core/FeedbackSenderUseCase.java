package br.com.adatech.feedbackmanager.core;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;


/** O que a aplicação faz - casos de uso - regras de negócio **/
public interface FeedbackSenderUseCase {

    /**
     * Recebe como parâmetro CustomerFeedback que serão enfileirados e processados
     * conforme a ordem em que foram recebidos.
     * **/
    void sendCustomerFeedback(CustomerFeedback customerFeedback);


}