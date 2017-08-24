package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.ChangeActionNotifier;
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
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;

public class AnalysisTraceToolWindow extends RemediationToolWindowBase {

    private void gotoLine(int lineNumber) {

        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        FileEditorManager fem = FileEditorManager.getInstance(project);

        Editor selectedTextEditor = fem.getSelectedTextEditor();
        CaretModel caretModel = selectedTextEditor.getCaretModel();

        caretModel.moveToLogicalPosition(new LogicalPosition(lineNumber-1,0));

        ScrollingModel scrollingModel = selectedTextEditor.getScrollingModel();
        scrollingModel.scrollToCaret(ScrollType.CENTER);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(headerLabel, BorderLayout.NORTH);

        JBList<String> list = new JBList<String>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Go to line: 1");
        listModel.addElement("Go to line: 15");
        listModel.addElement("Go to line: 25");
        listModel.addElement("Go to line: 35");
        listModel.addElement("Go to line: 50");
        listModel.addElement("Go to line: 99");
        list.setModel(listModel);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String selectedValue = list.getSelectedValue();
                String lineNum = selectedValue.substring(selectedValue.indexOf(":")+1).trim();
                gotoLine(Integer.parseInt(lineNum));
            }
        });
        panel.add(new JBScrollPane(list), BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel,"",false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    protected void onIssueChange(String msg) {
        headerLabel.setText("!!!:"+msg);
    }
}
