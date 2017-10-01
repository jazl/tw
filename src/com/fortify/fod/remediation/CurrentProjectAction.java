package com.fortify.fod.remediation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;

public class CurrentProjectAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        showInMemoryDocument(project);
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
