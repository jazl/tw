package com.fortify.fod.remediation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class CheckStatusAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        RemediationProjectComponent component = project.getComponent(RemediationProjectComponent.class);
        Messages.showInfoMessage("Is logged on? "+component.isLoggedIn(), "Status");
    }
}
