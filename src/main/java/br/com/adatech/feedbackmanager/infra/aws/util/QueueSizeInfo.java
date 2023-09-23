package br.com.adatech.feedbackmanager.infra.aws.util;

import java.util.Map;

public class QueueSizeInfo {
    private Map<String, QueueInfo> topics;
    private int totalSize;

    public Map<String, QueueInfo> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, QueueInfo> topics) {
        this.topics = topics;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
}
