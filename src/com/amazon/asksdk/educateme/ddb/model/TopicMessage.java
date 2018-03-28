package com.amazon.asksdk.educateme.ddb.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TopicMessage {

    private String topicId;
    private String topic;
    private String id;
    private String fact;

    @JsonCreator
    public TopicMessage(String topicId, String topic, String id, String fact) {
        this.topicId = topicId;
        this.topic = topic;
        this.id = id;
        this.fact = fact;
    }

    @Override
    public String toString() {
        return "TopicMessage{" +
            "topicId='" + topicId + '\'' +
            ", topic='" + topic + '\'' +
            ", id='" + id + '\'' +
            ", fact='" + fact + '\'' +
            '}';
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }
}
