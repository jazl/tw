package com.fortify.fod.remediation;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.messages.MessageBus;

public class RemediationPluginService {
    MessageBus messageBus = null;
    ChangeActionNotifier publisher = null;

    public RemediationPluginService() {
        Application application = ApplicationManager.getApplication();
        messageBus = application.getMessageBus();
        publisher = messageBus.syncPublisher(ChangeActionNotifier.CHANGE_ACTION_TOPIC);
    }

    public String getProject() {
        return null;
    }

    public void publishIssueChange(String selectedIssue){
        publisher.onIssueChanged(selectedIssue);
    }

    public void publishFoDProjectChange(String msg){
        publisher.onProjectChanged(msg);
    }

}
