package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.ChangeActionNotifier;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;

public class AnalysisResultsToolWindow extends RemediationToolWindowBase {

    MessageBus messageBus = null;
    ChangeActionNotifier publisher = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        class FrioritySummary {
            public FrioritySummary(String name, int cnt){
                this.friorityName = name;
                this.issueCount = cnt;
            }
            public String friorityName;
            public int issueCount;
            public Image priorityImage;
        }
        // Webgoat
        FrioritySummary[] tabs = new FrioritySummary[]{
            new FrioritySummary("Critical",44),
            new FrioritySummary("High",156),
            new FrioritySummary("Medium",1),
            new FrioritySummary("Low",1),
            new FrioritySummary("Info",2),
            new FrioritySummary("All",207),
        };

//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//
//        for(FrioritySummary f:tabs){
//            Content formContent = contentFactory.createContent(getTreePanel(),String.valueOf(f.issueCount),false);
//            toolWindow.getContentManager().addContent(formContent);
//        }

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(headerLabel, BorderLayout.NORTH);

        JBTabbedPane tab = new JBTabbedPane();
        for(FrioritySummary f:tabs) {
            tab.add(String.valueOf(f.issueCount),getTreePanel());
        }
        panel.add(tab,BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel,"",false);
        toolWindow.getContentManager().addContent(content);
    }

    private JPanel getTreePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        createNodes(root);
        Tree tree = new Tree(root);
        tree.setRootVisible(false);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(IconLoader.getIcon("/icons/trace/Generic.png"));
        tree.setCellRenderer(renderer);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                showFileInEditor();
                publisher.afterAction(selectedNode.toString());

            }
        });

        panel.add(new JBScrollPane(tree), BorderLayout.CENTER);
        return panel;
    }

    private DefaultMutableTreeNode createCategoryNodeAndChildren(String catNodeName, HashMap<String, ArrayList> vulnerabilities) {
        final DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(catNodeName);

        vulnerabilities.forEach(new BiConsumer<String, ArrayList>() {
            @Override
            public void accept(String vulnName, ArrayList traces) {
                DefaultMutableTreeNode vulnNodes = new DefaultMutableTreeNode(vulnName);
                for(int i=0; i<traces.size(); i++) {
                    vulnNodes.add(new DefaultMutableTreeNode(traces.get(i)));
                }
                catNode.add(vulnNodes);
            }
        });

        return catNode;
    }

    private void createNodes(DefaultMutableTreeNode root) {
        HashMap<String, ArrayList> vulnerabilities;
        ArrayList<String> traceNodes;

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        vulnerabilities = new HashMap<>();
        vulnerabilities.put("Exec.java:103", traceNodes);
        root.add(createCategoryNodeAndChildren("Command Injection (3)", vulnerabilities));

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        vulnerabilities = new HashMap<>();
        vulnerabilities.put("Exec.java:103", traceNodes);
        root.add(createCategoryNodeAndChildren("Cookie Security: Cookie not Sent Over SSL (2)", vulnerabilities));

        root.add(createCategoryNodeAndChildren("Log Forging (2)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Null Reference(2)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Null Reference(107)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Password Management: Empty Password (3)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Password Management: Hardcoded Password (13)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Password Management: Password in Configuration File (1)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Privacy Violation (18)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("SQL Injection (11)", vulnerabilities));
        root.add(createCategoryNodeAndChildren("Weak Encryption (4)", vulnerabilities));

    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        Application application = ApplicationManager.getApplication();
        messageBus = application.getMessageBus();
        publisher = messageBus.syncPublisher(ChangeActionNotifier.CHANGE_ACTION_TOPIC);
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
        LightVirtualFile vf = new LightVirtualFile("LightVirtualFile.java", "What the deuce?!??");
        OpenFileDescriptor fd = new OpenFileDescriptor(project, vf);

        //fd.navigate(true);
        fem.openEditor(fd, true);
        //fem.openFile(vf, true   );
    }

}
