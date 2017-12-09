package com.fortify.fod.remediation.demo;

import com.fortify.fod.remediation.custom.GroupTreeItem;
import com.fortify.fod.remediation.custom.IssueTreeItem;
import com.fortify.fod.remediation.custom.VulnNodeCellRender;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by jazl on 10/27/17.
 */
public class TreeStuff extends JFrame {

    private Tree tree;
    private DefaultMutableTreeNode root;
    private TreePath rootPath;

    private Set<TreePath> expandedTreePaths = new HashSet<>();

    private Enumeration<TreePath> expandedDescendants;

    public TreeStuff() {

        initData();
        setSize(600,600);
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(getTreePanel(), BorderLayout.CENTER);
        panel.add(getControlsPanel(), BorderLayout.SOUTH);

        add(panel);
    }

    private Component getTreePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5,5,5,5));

        tree = new Tree();

        root = new DefaultMutableTreeNode();
        TreeModel model = new DefaultTreeModel(root);

        createCategoryNodes(root,1234);

        tree.setModel(model);
        tree.setCellRenderer(new VulnNodeCellRender());
        rootPath = new TreePath(root);

        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                expandedTreePaths.add(event.getPath());
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                expandedTreePaths.remove(event.getPath());
            }
        });

        model.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                System.out.println("treeNodesChanged "+e.getTreePath().getLastPathComponent());
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                System.out.println("treeNodesInserted "+e.getTreePath().getLastPathComponent());

            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                System.out.println("treeNodesRemoved "+e.getTreePath().getLastPathComponent());

            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                System.out.println("treeStructureChanged "+e.getTreePath().getLastPathComponent());

            }
        });

        tree.setComponentPopupMenu(new TreePopUpMenu());
        panel.add(new JBScrollPane(tree), BorderLayout.CENTER);

        return panel;
    }

    class TreePopUpMenu extends JPopupMenu {
        public TreePopUpMenu() {
            add(new JMenuItem("Expand all"));
            add(new JMenuItem("Collapse all"));
            add(new JMenuItem("Refresh"));
            addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    System.out.println("popupMenuWillBecomeVisible");
                    TreePopUpMenu source = (TreePopUpMenu) e.getSource();
                    JTree invoker = (JTree) source.getInvoker();
                    TreePath selectionPath = invoker.getSelectionPath();
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {

                }
            });
        }
    }
    private Component getControlsPanel() {
        JPanel panel = new JPanel();

        JButton testButton = new JButton("Save Opened Nodes");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("openButton clicked, size = "+expandedTreePaths.size());
                expandedDescendants = tree.getExpandedDescendants(rootPath);
            }
        });

        JButton openButton = new JButton("Open Nodes");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(expandedDescendants!=null) {
                    while (expandedDescendants.hasMoreElements()) {
                        TreePath treePath = expandedDescendants.nextElement();
                        if(treePath != null) {
                            System.out.println("expanding path: "+treePath);
                            tree.expandPath(treePath);
                        }
                    }
                }
                else {
                    System.out.println("No opened nodes were saved");
                }
            }
        });

        JButton deleteButton = new JButton("Delete Node");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node;
                DefaultTreeModel model = (DefaultTreeModel) (tree.getModel());
                TreePath[] paths = tree.getSelectionPaths();
                for (int i = 0; i < paths.length; i++) {
                    node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
                    model.removeNodeFromParent(node);
                }
            }
        });

        panel.add(testButton);
        panel.add(openButton);
        panel.add(deleteButton);

        return panel;
    }

    private void createCategoryNodes(DefaultMutableTreeNode root, int hashCode) {

        HashMap<String, ArrayList> issues;
        ArrayList<String> traceNodes;

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        issues = new HashMap<>();
        issues.put("Exec.java:103", traceNodes);
        for(int i=1; i<=50; i++) {
            issues.put("Exec.java:"+i, traceNodes);
        }
        root.add(createGroupingNodeAndChildren("Command Injection (3) - "+hashCode, issues));

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        issues = new HashMap<>();
        issues.put("Exec.java:103", traceNodes);

        categoriesList.forEach(cat -> root.add(createCategoryNode(cat)));
    }

    private DefaultMutableTreeNode createCategoryNode(GroupTreeItem categoryItem) {
        final DefaultMutableTreeNode groupingNode = new DefaultMutableTreeNode(categoryItem);
        return groupingNode;
    }

    private DefaultMutableTreeNode createGroupingNodeAndChildren(String groupingNodeName, HashMap<String, ArrayList> issues) {
        final DefaultMutableTreeNode groupingNode = new DefaultMutableTreeNode(groupingNodeName);
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

    public static void main(String[] args) {
        TreeStuff ts = new TreeStuff();
        ts.setVisible(true);
    }

    class CategoryItem {
        private int categoryId;
        private String categoryName;

        public CategoryItem(int categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }

    private void initData() {
        categoriesList = new ArrayList<>();
        categoriesList.add(new GroupTreeItem("100", "Cookie Security: Cookie not Sent Over SSL (2)"));
        categoriesList.add(new GroupTreeItem("200", "Log Forging (2))"));
        categoriesList.add(new GroupTreeItem("300", "Null Reference"));
        categoriesList.add(new GroupTreeItem("400", "Password Management: Empty Password"));
        categoriesList.add(new GroupTreeItem("500", "Password Management: Hardcoded Password (13)"));
        categoriesList.add(new GroupTreeItem("600", "Password Management: Password in Configuration File"));
        categoriesList.add(new GroupTreeItem("700", "Privacy Violation (18)"));
        categoriesList.add(new GroupTreeItem("800", "SQL Injection (11)"));
        categoriesList.add(new GroupTreeItem("900", "Weak Encryption (4)"));
    }
    private ArrayList<GroupTreeItem> categoriesList;

}
