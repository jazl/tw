package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTabbedPane;
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
        JPanel panel = getDefaultToolWindowContentPanel();

        panel.add(headerLabel, BorderLayout.NORTH);

        JBTabbedPane tab = new JBTabbedPane();
        tab.addTab("Details", createDetailsContent());
        tab.addTab("Recommendations", createRecommendationsContent());
        panel.add(tab, BorderLayout.CENTER);

        addContent(toolWindow, panel);
    }

    @Override
    protected void onIssueChange(IssueChangeInfo changeInfo) {
        headerLabel.setText(changeInfo.getIssueName());
    }

    @Override
    protected void onFoDProjectChange(String msg) {
        toggleContent();
    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Issue Summary");
    }
}
