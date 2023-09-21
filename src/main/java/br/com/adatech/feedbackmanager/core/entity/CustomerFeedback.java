package br.com.adatech.feedbackmanager.core.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class CustomerFeedback {

    @Id
    private String uuid;
    private String message;
    private FeedbackType type;
    private FeedbackStatus status;

    public CustomerFeedback(){
        status = FeedbackStatus.received;
    }
    public CustomerFeedback(String message, FeedbackType type)
    {
        this();
        this.uuid = UUID.randomUUID().toString();
        this.message = message;
        this.type = type;
    }

    public String getUuid(){ return uuid; }

    public void setMessage(String message){ this.message = message; }

    public String getMessage(){ return message; }

    public void setType(FeedbackType type){ this.type = type; }

    public FeedbackType getType(){ return type; }

    public void setStatus(FeedbackStatus status){ this.status = status; }

    public FeedbackStatus getStatus(){ return status; }
}
