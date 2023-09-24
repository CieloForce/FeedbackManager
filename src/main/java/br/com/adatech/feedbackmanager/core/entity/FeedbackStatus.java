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

    public static FeedbackStatus fromInteger(int value) {
        return switch (value) {
            case 0 -> received;
            case 1 -> processing;
            case 2 -> finished;
            default -> throw new IllegalArgumentException("Valor inválido para FeedbackStatus: " + value);
        };
    }
}
