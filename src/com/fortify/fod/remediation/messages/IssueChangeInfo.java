package com.fortify.fod.remediation.messages;

public class IssueChangeInfo {
    private String issueId;
    private String issueName;

    public IssueChangeInfo(String issueId, String issueName) {
        this.issueId = issueId;
        this.issueName = issueName;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }
}
