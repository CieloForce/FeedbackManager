package br.com.adatech.feedbackmanager.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CustomerFeedbackNotFoundException  extends RuntimeException{
    public CustomerFeedbackNotFoundException(String id){
        super("Customer feedback not found with id: " + id);
    }
}
