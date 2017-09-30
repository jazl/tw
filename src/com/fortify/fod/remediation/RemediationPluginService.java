package com.fortify.fod.remediation;

import com.fortify.fod.remediation.messages.ChangeActionNotifier;
import com.fortify.fod.remediation.messages.IssueChangeInfo;
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

    public String[] getHistory(String issueId) {
        return new String[]{
            "test@test.com (Aug 21, 2017 10:24:46 AM): Changed Developer status to 'Will Not Fix'",
            "test@test.com (Aug 21, 2017 10:24:46 AM): Changed Developer status to 'Will Not Fix'",
            "test@test.com (Aug 21, 2017 10:24:46 AM): Changed Developer status to 'Will Not Fix'"
        };
    }

    public String getRecommendations(String issueId) {
        return "blah";
    }

    public String getIssueDetails(String issueId) {
        return "blah";
    }

    public String getAuditDetails(String issueId) {
        return "blah";
    }

    public String[] getComments(String issueId) {
        return new String[]{
            "captain_oveur@test.com (Aug 11, 2017 10:24:46 AM):  Roger, Roger. What's our vector, Victor?",
            "test_test@test.com (Aug 12, 2017 11:00:55 AM): blah, blah, blah, blah",
            "testing@test.com (Aug 15, 2017 2:33:10 PM): Veni vidi vici",
            "ivanka@microfocus.com (Aug 25, 2017 3:33:12 AM): testing testing testing... long comment",
            "rumack@hpe.com (Aug 25, 2017 4:03:01 AM): I am serious... and don't call me Shirley",
        };
    }

    public void publishIssueChange(String selectedIssue){
        publisher.onIssueChanged(new IssueChangeInfo("1234", selectedIssue));
    }

    public void publishFoDProjectChange(String msg){
        publisher.onProjectChanged(msg);
    }

    public void publicLogoff(String msg){
        publisher.onLogoff();
    }

}
