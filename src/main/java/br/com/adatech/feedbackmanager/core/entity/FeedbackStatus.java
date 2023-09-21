package br.com.adatech.feedbackmanager.core.entity;

public enum FeedbackStatus {
    received("Recebido"),
    processing("Em processamento"),
    finished("Finalizado");

    private final String description;

    FeedbackStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
