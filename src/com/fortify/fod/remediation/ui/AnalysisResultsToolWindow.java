package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
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

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        super.createToolWindowContent(project, toolWindow);
        System.out.println("in AnalysisResultsToolWindow createToolWindowContent");

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

        JPanel panel = getDefaultToolWindowContentPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[]{1.0};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
        panel.setLayout(gridBagLayout);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        panel.add(headerLabel, gridBagConstraints);

        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0,5,0,5);
        panel.add(getFilterPanel(), gridBagConstraints);

        JBTabbedPane tab = new JBTabbedPane();
        for(FrioritySummary f:tabs) {
            tab.add(String.valueOf(f.issueCount),getTreePanel());
        }
        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        panel.add(tab, gridBagConstraints);

        addContent(toolWindow, panel);
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
                remediationPluginService.publishIssueChange(selectedNode.toString());
            }
        });

        panel.add(new JBScrollPane(tree), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getFilterPanel() {
        JPanel filterPanel = new JPanel();

        GridBagLayout gbl_filterPanel = new GridBagLayout();
        gbl_filterPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_filterPanel.rowHeights = new int[]{29, 0};
        gbl_filterPanel.columnWeights = new double[]{0.0, 2.0, 0.0, Double.MIN_VALUE};
        gbl_filterPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        filterPanel.setLayout(gbl_filterPanel);

        JLabel lblGroupBy = new JLabel("Group By: ");
        GridBagConstraints gbc_lblGroupBy = new GridBagConstraints();
        gbc_lblGroupBy.insets = new Insets(0, 0, 0, 5);
        gbc_lblGroupBy.fill = GridBagConstraints.BOTH;
        gbc_lblGroupBy.gridx = 0;
        gbc_lblGroupBy.gridy = 0;
        filterPanel.add(lblGroupBy, gbc_lblGroupBy);

        JComboBox<String> groupByCombo = new ComboBox<>();
        DefaultComboBoxModel<String> groupByModel = new DefaultComboBoxModel<>();
        groupByModel.addElement("analysisType");
        groupByModel.addElement("assignedUser");
        groupByModel.addElement("auditPendingAuditorStatus");
        groupByModel.addElement("auditorStatus");
        groupByModel.addElement("category");
        groupByCombo.setModel(groupByModel);
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.insets = new Insets(0, 0, 0, 5);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 0;
        filterPanel.add(groupByCombo, gbc_comboBox);

        JButton optionsButton = new JButton();
        optionsButton.setIcon(IconLoader.getIcon("/icons/options.png"));
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.gridx = 2;
        gbc_btnNewButton.gridy = 0;
        filterPanel.add(optionsButton, gbc_btnNewButton);

        return filterPanel;
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
