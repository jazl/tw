package com.azl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AnalysisTraceToolWindow implements ToolWindowFactory {

    JLabel messageLabel;
    JTextField txtLineNumber;

    private void gotoLine(int lineNumber) {
        System.out.println("Going to line "+lineNumber);

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
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        panel.add(new JLabel("<html><h2>Analysis Trace</h2></html>"), c);

        c.gridy = c.gridy+1;
        messageLabel = new JLabel("Messages go here!");
        panel.add(messageLabel, c);

//        c.gridy = c.gridy+1;
//        txtLineNumber = new JTextField(10);
//        txtLineNumber.setText("5");
//        panel.add(txtLineNumber, c);
//
//        c.gridy = c.gridy+1;
//        JButton goButton = new JButton("Go To Line");
//        goButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                gotoLine(Integer.parseInt(txtLineNumber.getText()));
//            }
//        });
//        panel.add(goButton, c);

        c.gridy = c.gridy+1;
        c.fill = GridBagConstraints.HORIZONTAL;
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
        panel.add(new JBScrollPane(list), c);

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
