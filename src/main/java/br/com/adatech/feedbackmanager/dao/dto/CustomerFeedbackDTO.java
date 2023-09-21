package br.com.adatech.feedbackmanager.dao.dto;

import br.com.adatech.feedbackmanager.core.entity.FeedbackType;

public record CustomerFeedbackDTO(String message, String type) {
}
