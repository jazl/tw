package com.azl;

import com.intellij.util.messages.Topic;

import javax.naming.Context;

public interface ChangeActionNotifier {

    Topic<ChangeActionNotifier> CHANGE_ACTION_TOPIC = Topic.create("custom name", ChangeActionNotifier.class);

    void beforeAction(String message);
    void afterAction(String message);
}