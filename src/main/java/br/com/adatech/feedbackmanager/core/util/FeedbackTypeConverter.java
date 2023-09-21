package br.com.adatech.feedbackmanager.core.util;

import br.com.adatech.feedbackmanager.core.entity.FeedbackType;

public class FeedbackTypeConverter {
    public static FeedbackType fromString(String input) {
        for (FeedbackType feedbackType : FeedbackType.values()) {
            if (feedbackType.getDescription().equalsIgnoreCase(input)) {
                return feedbackType;
            }
        }
        throw new IllegalArgumentException("Tipo de feedback inválido: " + input);
        //TO DO: Personalizar exceção para InvalidFeedbackTypeException
    }
}
