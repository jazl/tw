package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.ChangeActionNotifier;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;

public abstract class RemediationToolWindowBase implements ToolWindowFactory {

    JLabel headerLabel = new JLabel();

    public RemediationToolWindowBase() {
        headerLabel.setText("<Select Vulnerability>");
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        System.out.println("in RemediationToolWindowBase createToolWindowContent");
        // In subclass, call super and implement
    }

    @Override
    public void init(ToolWindow window) {

        // If implemented in subclass, call super!!

        Application application = ApplicationManager.getApplication();
        MessageBus bus = application.getMessageBus();

        bus.connect().subscribe(ChangeActionNotifier.CHANGE_ACTION_TOPIC, new ChangeActionNotifier() {
            @Override
            public void beforeAction(String msg) {
                System.out.println("Got beforeAction message: "+msg);
            }
            @Override
            public void afterAction(String msg) {
                onIssueChange(msg);
            }
        });
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }

    protected abstract void onIssueChange(String msg);

}
