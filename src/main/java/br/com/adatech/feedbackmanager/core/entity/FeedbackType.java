package br.com.adatech.feedbackmanager.core.entity;

public enum FeedbackType {

    suggestion("Sugestão"),
    compliment("Elogio"),
    criticism("Crítica");

    private final String description;
    FeedbackType(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }

}
