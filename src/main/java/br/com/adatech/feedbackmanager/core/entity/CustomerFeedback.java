package br.com.adatech.feedbackmanager.core.entity;

import jakarta.persistence.*;

@Entity
public class CustomerFeedback {

    @Id
    private String id;
    private String message;
    private FeedbackType type;
    private FeedbackStatus status;

    public CustomerFeedback(){
        status = FeedbackStatus.Recebido;
    }
    public CustomerFeedback(String message, FeedbackType type)
    {
        this();
        this.message = message;
        this.type = type;
    }

    public void setId(String id){ this.id = id; }

    public String getId(){ return id; }

    public void setMessage(String message){ this.message = message; }

    public String getMessage(){ return message; }

    public void setType(FeedbackType type){ this.type = type; }

    public FeedbackType getType(){ return type; }

    public void setStatus(FeedbackStatus status){ this.status = status; }

    public FeedbackStatus getStatus(){ return status; }
}
