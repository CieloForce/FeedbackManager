package br.com.adatech.feedbackmanager.entity;

public enum FeedbackStatus {
    Recebido("Recebido"),
    EmProcessamento("Em processamento"),
    Finalizado("Finalizado");

    private String description;

    FeedbackStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
