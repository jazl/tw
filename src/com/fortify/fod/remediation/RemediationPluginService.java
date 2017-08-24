package com.fortify.fod.remediation;

public class RemediationPluginService {
    private String project = "testing";
    public RemediationPluginService() {
        System.out.println("Creating RemediationPluginService");
    }

    public String getProject() {
        return project;
    }
}
