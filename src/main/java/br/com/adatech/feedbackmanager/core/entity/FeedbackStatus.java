package br.com.adatech.feedbackmanager.core.entity;

public enum FeedbackStatus {
    received("Received"),
    processing("Processing"),
    finished("Finished");

    private final String description;

    FeedbackStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
