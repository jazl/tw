package com.azl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class AuditSummaryToolWindow implements ToolWindowFactory {

    JLabel messageLabel;
    JTextField txtLineNumber;

    private JPanel createAuditContent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("Audit content goes here"));
        return panel;
    }
    private JPanel createHistoryContent(){
        JPanel panel = new JPanel();
        panel.add(new JLabel("History content goes here"));
        return panel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JBTabbedPane tab = new JBTabbedPane();
        tab.addTab("Audit", createAuditContent());
        tab.addTab("History", createHistoryContent());

        panel.add(tab, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel,"",false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(ToolWindow window) {
        Application application = ApplicationManager.getApplication();
        MessageBus bus = application.getMessageBus();

        bus.connect().subscribe(ChangeActionNotifier.CHANGE_ACTION_TOPIC, new ChangeActionNotifier() {
            @Override
            public void beforeAction(String msg) {
                System.out.println("Got beforeAction message: "+msg);
            }
            @Override
            public void afterAction(String msg) {
                System.out.println("Got afterAction message: "+msg);
                messageLabel.setText(msg);
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
}
