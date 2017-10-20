package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AuditSummaryToolWindow extends RemediationToolWindowBase {

    private JBList<String> historyList = null;
    private JBList<String> commentList = null;

    private JPanel createAuditContent(){
        JPanel panel = new JPanel(new BorderLayout());

        JBSplitter splitter = new JBSplitter(1.0F);
        splitter.setFirstComponent(createAuditDetailsPanel());
        splitter.setSecondComponent(createCommentsPanel());
        splitter.setProportion(0.5F);

        panel.add(splitter, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAuditDetailsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
        addAuditDetailField(fieldsPanel, "User", new String[]{""});
        addAuditDetailField(fieldsPanel, "DeveloperStatus", new String[]{""});
        addAuditDetailField(fieldsPanel, "AuditorStatus", new String[]{""});
        addAuditDetailField(fieldsPanel, "Severity", new String[]{""});
        mainPanel.add(fieldsPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    private void addAuditDetailField(JPanel panel, String labelText, String[] optionsList) {
        // TODO: next 3 lines are a hack to work around BoxLayout label alignment weirdness
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(new JLabel(labelText), BorderLayout.CENTER);
        labelPanel.setBorder(new EmptyBorder(new Insets(5,0,5,0)));
        panel.add(labelPanel);

        ComboBox options = new ComboBox(optionsList);
        //options.setBorder(new EmptyBorder(new Insets(0,0,10,0)));
        panel.add(options);
    };

    private JPanel createCommentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel commentsLabel = new JLabel("Comments");
        commentsLabel.setBorder(new EmptyBorder(new Insets(5,0,5,0)));
        panel.add(commentsLabel, BorderLayout.NORTH);

        commentList = new JBList<>();
        commentList.setModel(getHistory(null));
        panel.add(new JBScrollPane(commentList), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHistoryContent(){
        JPanel panel = new JPanel(new BorderLayout());

        historyList = new JBList<>();
        historyList.setModel(getHistory(null));
        panel.add(new JBScrollPane(historyList), BorderLayout.CENTER);

        return panel;
    }

    private DefaultListModel<String> getHistory(String issueId) {
        DefaultListModel<String> model = new DefaultListModel<>();
        String[] history = issueId != null ? remediationPluginService.getHistory(issueId) : new String[]{};
        for(String h:history) {
            model.addElement(h);
        }
        return model;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = getDefaultToolWindowContentPanel();

        panel.add(headerLabel, BorderLayout.NORTH);

        JBTabbedPane tab = new JBTabbedPane();
        tab.addTab("Audit", createAuditContent());
        tab.addTab("History", createHistoryContent());

        panel.add(tab, BorderLayout.CENTER);

        addContent(toolWindow, panel);

        toolWindow.setTitle(project.getName()+" - "+this.hashCode());

    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Audit Summary");
    }

    @Override
    protected void onIssueChange(IssueChangeInfo changeInfo) {
        headerLabel.setText(changeInfo.getIssueName());
        historyList.setModel(getHistory(changeInfo.getIssueId()));
    }

    @Override
    protected void onFoDProjectChange(String msg) {
        toggleContent();
    }
}
