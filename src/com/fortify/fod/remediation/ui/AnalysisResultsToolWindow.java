package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.custom.GroupTreeItem;
import com.fortify.fod.remediation.custom.IssueTreeItem;
import com.fortify.fod.remediation.custom.VulnNodeCellRender;
import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.fortify.fod.remediation.models.VulnFolder;
import com.intellij.execution.Executor;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.JBCheckboxMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBMenu;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonatype.nexus.index.treeview.DefaultTreeNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.function.BiConsumer;

public class AnalysisResultsToolWindow extends RemediationToolWindowBase {

    private VulnTabbedPane tabbedPane;
    private DefaultTreeModel treeModel;
    private Tree issuesTree;

    private static int counter = 0;

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
        for(VulnFolder f:folders) {
            Component component = tabbedPane.add(f.getTitle(), getTabContents());
        }
        panel.add(tabbedPane, BorderLayout.CENTER);

        addContent(toolWindow, panel);
    }

    private JPanel getTabContents() {
        JPanel panel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[]{1.0};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
        panel.setLayout(gridBagLayout);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        JLabel folderLabel = new JLabel("High (44)");
        folderLabel.setBackground(Color.ORANGE);
        folderLabel.setOpaque(true);
        folderLabel.setBorder(new EmptyBorder(new Insets(5,5,5,0)));
        panel.add(folderLabel, gridBagConstraints);

        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        panel.add(getFilterPanel(), gridBagConstraints);

        gridBagConstraints.gridy = gridBagConstraints.gridy+1;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panel.add(getTree(), gridBagConstraints);

        return panel;
    }

    private JBScrollPane getTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);
        createNodes(root, treeModel.hashCode());
        issuesTree = new Tree(treeModel);
        issuesTree.setRootVisible(false);

        //DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        //renderer.setLeafIcon(IconLoader.getIcon("/icons/trace/Generic.png"));

        VulnNodeCellRender r = new VulnNodeCellRender();
        issuesTree.setCellRenderer(r);

        issuesTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                handleTreeWillExpand(event);
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
        issuesTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                Object userObject = lastPathComponent.getUserObject();
                System.out.println("TreeSelectionListener, valueChanged on "+userObject.getClass().getTypeName()+", "+lastPathComponent.toString());
                //showFileInEditor();
                //remediationPluginService.publishIssueChange(selectedNode.toString());
            }
        });

        return new JBScrollPane(issuesTree);
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
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPopupList(optionsButton);
                //createPopupMenu(optionsButton);
                //createCheckboxPopupMenu(optionsButton);

//                JBMenu menu = new JBMenu();
//                menu.add(new JBCheckboxMenuItem("Test 1"));
//                menu.add(new JBCheckboxMenuItem("Test 2"));
//                menu.setVisible(true);
//
//
            }
        });

        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.gridx = 2;
        gbc_btnNewButton.gridy = 0;
        filterPanel.add(optionsButton, gbc_btnNewButton);

        return filterPanel;
    }

    private void createPopupMenu(Component invoker){
        JBPopupMenu menu = new JBPopupMenu();
        menu.setInvoker(invoker);
        menu.setLocation(invoker.getX(), invoker.getY());
        menu.add("Show suppressed");
        menu.add("Show fixed");
        menu.setVisible(true);
    }

    private void createCheckboxPopupMenu(Component invoker){
        JPopupMenu menu = new JPopupMenu();
        menu.setInvoker(invoker);
        Rectangle bounds = invoker.getBounds();
        menu.setLocation(invoker.getX(), bounds.y);
        menu.add(new JCheckBoxMenuItem("option 1"));
        menu.add(new JCheckBoxMenuItem("option 2"));
        menu.setVisible(true);
    }

    private void createPopupList(Component parentCtrl) {
//        JBList<TestAction> list = new JBList<>();
//        DefaultListModel<TestAction> listModel = new DefaultListModel<>();
//        listModel.addElement(new TestAction("Test 1", "", null));
//        listModel.addElement(new TestAction("Test 2", "", null));
//        list.setModel(listModel);

        JBList<String> list = new JBList<>();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Option 1");
        listModel.addElement("Option 2");
        list.setModel(listModel);

        list.setCellRenderer(new ToggleMenuItemCellRenderer());

        JBPopupFactory.getInstance().createListPopupBuilder(list)
            .setMovable(false)
            .setResizable(false)
            .setRequestFocus(true)
            .setItemChoosenCallback(() -> {
                //final TestAction value = (TestAction)list.getSelectedValue();
                //System.out.println("got value = "+value);
                //value.setSelected(null,true);
//                if ("Change issues tree".equals(value)) {
//                    changeIssuesTree();
//                }
//                if (value instanceof Executor) {
//                }
            }).createPopup().showUnderneathOf(parentCtrl);
    }

    private DefaultMutableTreeNode createGroupingNodeAndChildren(String groupingNodeName, HashMap<String, ArrayList> issues) {
        final DefaultMutableTreeNode groupingNode = new DefaultMutableTreeNode(String.valueOf(counter++) +" - " + groupingNodeName);
        groupingNode.setUserObject(new GroupTreeItem(groupingNodeName, groupingNodeName));

        issues.forEach(new BiConsumer<String, ArrayList>() {
            @Override
            public void accept(String issueName, ArrayList traces) {

                DefaultMutableTreeNode issueNode = new DefaultMutableTreeNode(issueName);
                issueNode.setUserObject(new IssueTreeItem(issueName, issueName));

                for(int i=0; i<traces.size(); i++) {
                    issueNode.add(new DefaultMutableTreeNode(traces.get(i)));
                }
                groupingNode.add(issueNode);
            }
        });

        return groupingNode;
    }

    private void createNodes(DefaultMutableTreeNode root, int hashCode) {

        HashMap<String, ArrayList> issues;
        ArrayList<String> traceNodes;

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        issues = new HashMap<>();
        issues.put("Exec.java:103", traceNodes);
        root.add(createGroupingNodeAndChildren("Command Injection (3) - "+hashCode, issues));

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        issues = new HashMap<>();
        issues.put("Exec.java:103", traceNodes);
        root.add(createGroupingNodeAndChildren("Cookie Security: Cookie not Sent Over SSL (2) - "+hashCode, issues));

        root.add(createGroupingNodeAndChildren("Log Forging (2)", issues));
        root.add(createGroupingNodeAndChildren("Null Reference(2)", issues));
        root.add(createGroupingNodeAndChildren("Null Reference(107)", issues));
        root.add(createGroupingNodeAndChildren("Password Management: Empty Password (3)", issues));
        root.add(createGroupingNodeAndChildren("Password Management: Hardcoded Password (13)", issues));
        root.add(createGroupingNodeAndChildren("Password Management: Password in Configuration File (1)", issues));
        root.add(createGroupingNodeAndChildren("Privacy Violation (18)", issues));
        root.add(createGroupingNodeAndChildren("SQL Injection (11)", issues));
        root.add(createGroupingNodeAndChildren("Weak Encryption (4)", issues));

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

    private void changeIssuesTree() {
        DefaultTreeModel treeModel = (DefaultTreeModel)issuesTree.getModel();
        int i = treeModel.hashCode();
        System.out.println("Changing issues tree with mode hashCode "+i);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();
        treeModel.reload();
        System.out.println("Removed all children!!");
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

    class ToggleMenuItemCellRenderer implements ListCellRenderer {

        JBCheckboxMenuItem renderer = new JBCheckboxMenuItem();
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            renderer.setText(String.valueOf(value)+"...");
            //renderer.setIcon(IconLoader.getIcon("/icons/trace/Generic.png"));
            return renderer;
        }
    }

    private void handleTreeWillExpand(TreeExpansionEvent event) {
        TreePath path = event.getPath();

        DefaultMutableTreeNode pathComponent = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();

        updateTreeNode(pathComponent);

//        DefaultMutableTreeNode child = (DefaultMutableTreeNode)treeModel.getChild(treeModel.getRoot(), 1);
//        System.out.println("child = "+child);

        //DefaultMutableTreeNode source = (DefaultMutableTreeNode)event.getSource();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)issuesTree.getLastSelectedPathComponent();
        DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        System.out.println("TreeWillExpandListener, treeWillExpand, "+lastPathComponent.toString());
    }

    private void updateTreeNode(DefaultMutableTreeNode node) {
        List<String> data = generateTreeData();
        node.removeAllChildren();
        for(int i=0; i<data.size(); i++) {
            node.add(new DefaultMutableTreeNode(data.get(i)));
        }
        treeModel.reload(node);
    }

    private List<String> generateTreeData() {
        System.out.println("Generating some data...");
        ArrayList<String> list = new ArrayList<>();
        try {
            Thread.sleep(2000);
            for(int i=0; i<15; i++) {
                list.add(LocalTime.now() + " ... " + String.valueOf(i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Returning data...");
        return list;
    }
}
