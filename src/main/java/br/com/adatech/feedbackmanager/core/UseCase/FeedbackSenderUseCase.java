package br.com.adatech.feedbackmanager.core.UseCase;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;


/** O que a aplicação faz - casos de uso - regras de negócio **/
public interface FeedbackSenderUseCase {

    /**
     * Recebe como parâmetro CustomerFeedback que serão enfileirados
     * **/
    void sendCustomerFeedback(CustomerFeedback customerFeedback);


}