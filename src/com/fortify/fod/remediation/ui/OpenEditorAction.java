package com.fortify.fod.remediation.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;

public class OpenEditorAction extends AnAction {
    private String fileName = "filename.java";
    private Project project;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        project = anActionEvent.getProject();
        showInMemoryDocument();
    }
    private String getFileContent() {
        StringBuilder sb = new StringBuilder(100);
        for(int i=0; i<100; i++) {
            sb.append("Line "+i+": opened in project "+project.getName()+"\n");
        }
        String fileContent = sb.toString();
        return fileContent;
    }

    private void showInMemoryDocument() {
        FileEditorManager fem = FileEditorManager.getInstance(project);
        LightVirtualFile vf = new LightVirtualFile(fileName, getFileContent());

        FileType fileType = vf.getFileType();
        System.out.println("fileType = "+fileType);
        if(fileType==StdFileTypes.UNKNOWN){
            vf.setFileType(StdFileTypes.PLAIN_TEXT);
        }

        OpenFileDescriptor fd = new OpenFileDescriptor(project, vf);

        java.util.List<FileEditor> fileEditors = fem.openEditor(fd, true);
        System.out.println("fileEditors.size() = "+fileEditors.size());

        //FileEditor[] fileEditors1 = fem.openFile(vf, true);

        //Editor editor = fem.openTextEditor(fd, true);
        //System.out.println("Editor = "+editor);

    }

}
