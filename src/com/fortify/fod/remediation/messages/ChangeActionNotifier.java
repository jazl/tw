package com.fortify.fod.remediation.messages;

import com.intellij.util.messages.Topic;

public interface ChangeActionNotifier {

    Topic<ChangeActionNotifier> CHANGE_ACTION_TOPIC = Topic.create("Issue Changed", ChangeActionNotifier.class);

    void onProjectChanged(String message);
    void onIssueChanged(IssueChangeInfo changeInfo);

}