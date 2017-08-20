package com.azl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import org.jdesktop.swingx.action.ActionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AnalysisToolWindowFactory implements ToolWindowFactory {

    JLabel messageLabel;

    private void createNewFile() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        final String testFilePath = "D:\\work\\intellij-plugin\\plugins\\tw\\src\\com\\azl\\ShowTw.java";
        final VirtualFile testFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(testFilePath));

        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, testFile);

        FileEditorManager fem = FileEditorManager.getInstance(project);
        fem.openEditor(openFileDescriptor, true);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        panel.add(new JLabel("<html><h2>Analysis Tool Window</h2></html>"), c);

        c.gridy = c.gridy+1;
        messageLabel = new JLabel("Messages go here!");
        panel.add(messageLabel, c);

        c.gridy = c.gridy+1;
        JButton newFileButton = new JButton("New File");
        newFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewFile();
            }
        });
        panel.add(newFileButton, c);

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
