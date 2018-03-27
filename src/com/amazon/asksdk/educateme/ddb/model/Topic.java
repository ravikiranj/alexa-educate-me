package com.amazon.asksdk.educateme.ddb.model;

public class Topic {

    private String topic;
    private int topicNumber = -1;
    private int readPointer = -1;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(int topicNumber) {
        this.topicNumber = topicNumber;
    }

    public int getReadPointer() {
        return readPointer;
    }

    public void setReadPointer(int readPointer) {
        this.readPointer = readPointer;
    }

    @Override
    public String toString() {
        return "Topic{" +
            "topic='" + topic + '\'' +
            ", topicNumber=" + topicNumber +
            ", readPointer=" + readPointer +
            '}';
    }
}
