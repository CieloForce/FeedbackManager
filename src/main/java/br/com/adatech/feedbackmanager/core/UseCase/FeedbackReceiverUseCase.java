package br.com.adatech.feedbackmanager.core.UseCase;

import br.com.adatech.feedbackmanager.core.entity.CustomerFeedback;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public interface FeedbackReceiverUseCase {

  /** Pocessa os CustomerFeedbacks conforme a ordem em que foram recebidos. **/
  List<Message> receiveCustomerFeedback(String queueUrl, int maxMessages);
  void deleteMessage(String queueUrl, String receiptHandle);
}
