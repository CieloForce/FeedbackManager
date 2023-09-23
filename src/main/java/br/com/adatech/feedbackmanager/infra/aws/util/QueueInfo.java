package br.com.adatech.feedbackmanager.infra.aws.util;

public class QueueInfo {
    private int approximateNumberOfMessages;
    private int approximateNumberOfMessagesNotVisible;
    private int approximateNumberOfMessagesDelayed;
    private int totalSize;


    public int getApproximateNumberOfMessagesNotVisible() {
        return approximateNumberOfMessagesNotVisible;
    }

    public void setApproximateNumberOfMessagesNotVisible(int approximateNumberOfMessagesNotVisible) {
        this.approximateNumberOfMessagesNotVisible = approximateNumberOfMessagesNotVisible;
    }
    public int getApproximateNumberOfMessages() {
        return approximateNumberOfMessages;
    }

    public void setApproximateNumberOfMessages(int approximateNumberOfMessages) {
        this.approximateNumberOfMessages = approximateNumberOfMessages;
    }

    public int getApproximateNumberOfMessagesDelayed() {
        return approximateNumberOfMessagesDelayed;
    }

    public void setApproximateNumberOfMessagesDelayed(int approximateNumberOfMessagesDelayed) {
        this.approximateNumberOfMessagesDelayed = approximateNumberOfMessagesDelayed;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
}