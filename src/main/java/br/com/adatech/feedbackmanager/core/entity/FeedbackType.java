package br.com.adatech.feedbackmanager.core.entity;

public enum FeedbackType {

    suggestion("Sugestão"),
    compliment("Elogio"),
    criticism("Crítica");

    private String description;
    FeedbackType(String description){
        this.description = description;
    }

}
