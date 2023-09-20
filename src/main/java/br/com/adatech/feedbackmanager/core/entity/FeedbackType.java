package br.com.adatech.feedbackmanager.core.entity;

public enum FeedbackType {

    Sugestoes("Sugestões"),
    Elogios("Elogios"),
    Criticas("Críticas");

    private String description;
    FeedbackType(String description){
        this.description = description;
    }

}
