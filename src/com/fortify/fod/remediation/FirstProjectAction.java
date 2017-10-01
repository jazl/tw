package com.fortify.fod.remediation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FirstProjectAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        showInMemoryDocument(openProjects[0]);
    }

    private void showInMemoryDocument(Project project) {
        StringBuilder sb = new StringBuilder(100);
        for(int i=0; i<100; i++) {
            sb.append("Line "+i+": opened in project "+project.getName()+"\n");
        }
        String fileContent = sb.toString();

        EditorFactory ef = EditorFactory.getInstance();
        FileDocumentManager fdm = FileDocumentManager.getInstance();
        FileEditorManager fem = FileEditorManager.getInstance(project);

        Document document = ef.createDocument("test");
        VirtualFile file = fdm.getFile(document);

        LightVirtualFile vf = new LightVirtualFile("LightVirtualFile.java", fileContent);
        OpenFileDescriptor fd = new OpenFileDescriptor(project, vf);

        java.util.List<FileEditor> fileEditors = fem.openEditor(fd, true);
    }

}
