package com.amazon.asksdk.educateme.session.model;

import com.amazon.asksdk.restapi.ddb.model.Topic;
import com.amazon.speech.speechlet.User;

public class UserInfo {

    private User user;
    private Topic topic;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
