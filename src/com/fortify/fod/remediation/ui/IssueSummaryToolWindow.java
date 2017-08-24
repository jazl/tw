package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.ChangeActionNotifier;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class IssueSummaryToolWindow extends RemediationToolWindowBase {

    private JPanel createDetailsContent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Details go here"));
        return panel;
    }
    private JPanel createRecommendationsContent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Recommendations go here"));
        return panel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        panel.add(headerLabel, BorderLayout.NORTH);

        JBTabbedPane tab = new JBTabbedPane();
        tab.addTab("Details", createDetailsContent());
        tab.addTab("Recommendations", createRecommendationsContent());
        panel.add(tab, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel,"",false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    protected void onIssueChange(String msg) {
        headerLabel.setText("ISSUE SUMMARY:"+msg);
    }
}
