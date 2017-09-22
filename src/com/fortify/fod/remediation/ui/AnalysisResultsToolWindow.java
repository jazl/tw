package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.ChangeActionNotifier;
import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.fortify.fod.remediation.messages.ToolWindowActionListener;
import com.fortify.fod.remediation.models.VulnFolder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;

public class AnalysisResultsToolWindow extends RemediationToolWindowBase implements ToolWindowActionListener {

    private VulnTabbedPane tabbedPane;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super.createToolWindowContent(project, toolWindow);

        // Webgoat
        VulnFolder[] folders = new VulnFolder[]{
                new VulnFolder("Critical","Critical"),
                new VulnFolder("High","High"),
                new VulnFolder("Medium","Medium"),
                new VulnFolder("Low","Low"),
                new VulnFolder("Info","Info"),
                new VulnFolder("All","All")
        };

        JPanel panel = getDefaultToolWindowContentPanel();

        panel.add(headerLabel, BorderLayout.NORTH);

        tabbedPane = new VulnTabbedPane();

        DefaultSingleSelectionModel tabModel = new DefaultSingleSelectionModel();
        tabbedPane.setModel(tabModel);

        for(VulnFolder f:folders) {
            tabbedPane.addTab(f.getTitle(), new AnalysisResultsTabPanel(f, this));
            //tabbedPane.add(getTabContents());
        }

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                JPanel selectedPanel = (JPanel)sourceTabbedPane.getSelectedComponent();
                System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
                //folderLabel = (JLabel)selectedPanel.getComponent(0);
                //folderLabel.setText(LocalTime.now().toString());
            }
        });

        panel.add(tabbedPane, BorderLayout.CENTER);

        addContent(toolWindow, panel);
    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Analysis Results");
    }

    @Override
    protected void onIssueChange(IssueChangeInfo changeInfo) {
        headerLabel.setText(changeInfo.getIssueName());
    }

    @Override
    protected void onFoDProjectChange(String msg) {
        toggleContent();
    }

    private void showFileInEditor() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        final String testFilePath = project.getBasePath()+"\\src\\com\\azl2\\bigfile.java";

        final VirtualFile testFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(testFilePath));

        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, testFile);
        int line = openFileDescriptor.getLine();

        FileEditorManager fem = FileEditorManager.getInstance(project);
        java.util.List<FileEditor> fileEditors = fem.openEditor(openFileDescriptor, true);
    }

    LightVirtualFile vf = null;

    private void showInMemoryDocument() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/documents.html

        EditorFactory ef = EditorFactory.getInstance();
        FileDocumentManager fdm = FileDocumentManager.getInstance();
        FileEditorManager fem = FileEditorManager.getInstance(project);

        Document document = ef.createDocument("testing this thing!");
        VirtualFile file = fdm.getFile(document);

//        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, file);
//        fem.openEditor(openFileDescriptor, true);

        // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206132679/comments/206178995

        // An in-memory VF.
        if(vf == null) {
            vf = new LightVirtualFile("LightVirtualFile.java", "What the deuce?!??");
        }
        OpenFileDescriptor fd = new OpenFileDescriptor(project, vf);

        //fd.navigate(true);
        java.util.List<FileEditor> fileEditors = fem.openEditor(fd, true);
        FileEditor fileEditor = fileEditors.get(0);
        fileEditor.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("PropertyChangeEvent fired!!");
            }
        });

        Application application = ApplicationManager.getApplication();
        MessageBus bus = application.getMessageBus();

        bus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                System.out.println("test");
                if(file.getName().equals("LightVirtualFile.java")) {
//                    vf = null;
                }
            }
        });

        //fem.openFile(vf, true   );
    }

    class TestAction extends ToggleAction {

        public TestAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
            super(text, description, icon);
        }

        @Override
        public boolean isSelected(AnActionEvent anActionEvent) {
            return true;
        }

        @Override
        public void setSelected(AnActionEvent anActionEvent, boolean b) {

            System.out.println("set selected?");
        }
    }

    private void getFileInProject(){
        System.out.println("working with project "+this.project.getName());

        GlobalSearchScope globalSearchScope = new GlobalSearchScope() {
            @Override
            public int compare(@NotNull VirtualFile virtualFile, @NotNull VirtualFile virtualFile1) {
                return 0;
            }

            @Override
            public boolean isSearchInModuleContent(@NotNull Module module) {
                return true;
            }

            @Override
            public boolean isSearchInLibraries() {
                return false;
            }

            @Override
            public boolean contains(@NotNull VirtualFile virtualFile) {
                return true;
            }
        };

        String[] allFilenames = FilenameIndex.getAllFilenames(project);

        Collection<VirtualFile> virtualFilesByName = FilenameIndex.getVirtualFilesByName(project, "Class1.java", globalSearchScope);

        if(virtualFilesByName.isEmpty()) {
            Messages.showErrorDialog("Cannot find file", "Error");
        }
        else {
            VirtualFile virtualFile = virtualFilesByName.iterator().next();
            FileEditorManager fem = FileEditorManager.getInstance(project);
            OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, virtualFile);
            java.util.List<FileEditor> fileEditors = fem.openEditor(openFileDescriptor, true);
            //FileEditor thisEditor = fileEditors.get(0);
            System.out.println("test");
        }

        for(int i=0; i<allFilenames.length; i++){
            if(allFilenames[i].endsWith(".java")) {
                System.out.println(allFilenames[i]);
            }
        }
        String fileName = "GridBagLayout.java";

        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, fileName, globalSearchScope);
    }

    @Override
    public void getSource() {
        showInMemoryDocument();
    }
}
