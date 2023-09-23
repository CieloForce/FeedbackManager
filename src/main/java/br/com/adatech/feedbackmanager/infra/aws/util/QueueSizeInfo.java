package br.com.adatech.feedbackmanager.infra.aws.util;

import java.util.Map;

public class QueueSizeInfo {
    private Map<String, Map<String, Map<String, Integer>>> globalTopics;
    private Map<String, Integer> globalSize;

    public QueueSizeInfo(Map<String, Map<String, Map<String, Integer>>> globalTopics, Map<String, Integer> globalSize) {
        this.globalTopics = globalTopics;
        this.globalSize = globalSize;
    }
    public Map<String, Map<String, Map<String, Integer>>> getGlobalTopics() {
        return globalTopics;
    }

    public Map<String, Integer> getGlobalSize() {
        return globalSize;
    }

}
