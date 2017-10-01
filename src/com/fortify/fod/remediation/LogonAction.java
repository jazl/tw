package com.fortify.fod.remediation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class LogonAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        RemediationProjectComponent component = project.getComponent(RemediationProjectComponent.class);
        component.setLogin(true);
    }
}
