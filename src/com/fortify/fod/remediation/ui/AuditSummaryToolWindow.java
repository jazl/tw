package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JRadioButton rbNon = new JRadioButton("NON_MODAL");
        rbNon.setSelected(true);
        JRadioButton rbAny = new JRadioButton("any()");
        JRadioButton rbCurrent = new JRadioButton("current()");
        JRadioButton rbDefault = new JRadioButton("default()");
        JRadioButton rbForComponent = new JRadioButton("stateForComponent");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rbNon);
        buttonGroup.add(rbAny);
        buttonGroup.add(rbCurrent);
        buttonGroup.add(rbDefault);
        buttonGroup.add(rbForComponent);

        panel.add(rbNon);
        panel.add(rbAny);
        panel.add(rbCurrent);
        panel.add(rbDefault);
        panel.add(rbForComponent);

        JButton button1 = new JButton("ModalityState");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModalityState ms = ModalityState.NON_MODAL;
                if(rbAny.isSelected()) {
                    ms = ModalityState.any();
                }
                else if(rbCurrent.isSelected()) {
                    ms = ModalityState.current();
                }
                else if(rbDefault.isSelected()) {
                    ms = ModalityState.defaultModalityState();
                }
                else if(rbForComponent.isSelected()) {
                    ms = ModalityState.stateForComponent(AuditSummaryToolWindow.this.getDefaultToolWindowContentPanel());
                }
                System.out.println("MS: ModalityState = "+ms.toString());
                ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("MS: in run block...waiting");
                            Thread.sleep(3000);
                            System.out.println("MS: in run block...done");
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, ms);
                System.out.println("MS: after invokeAndWait");
            }
        });
        panel.add(button1);
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
