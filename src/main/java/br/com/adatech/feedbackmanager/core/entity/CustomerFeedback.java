package br.com.adatech.feedbackmanager.core.entity;

public class CustomerFeedback {
    private String uuid;
    private String message;
    private FeedbackType type;
    private FeedbackStatus status;

    public CustomerFeedback(String message, FeedbackType type) {
        this.uuid = uuid;
    }
}
