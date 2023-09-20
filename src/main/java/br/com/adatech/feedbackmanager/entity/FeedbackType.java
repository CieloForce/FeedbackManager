package br.com.adatech.feedbackmanager.entity;

public enum FeedbackType {

    Sugestoes("Sugestões"),
    Elogios("Elogios"),
    Criticas("Críticas");

    private String description;
    FeedbackType(String description){
        this.description = description;
    }

}
