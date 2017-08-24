package com.fortify.fod.remediation;

import com.intellij.util.messages.Topic;

public interface ChangeActionNotifier {

    Topic<ChangeActionNotifier> CHANGE_ACTION_TOPIC = Topic.create("Issue Changed", ChangeActionNotifier.class);

    void beforeAction(String message);
    void afterAction(String message);
}