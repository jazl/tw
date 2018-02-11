package com.fortify.fod.remediation.demo;

import com.fortify.fod.remediation.custom.GroupTreeItem;
import com.fortify.fod.remediation.custom.IssueTreeItem;
import com.fortify.fod.remediation.custom.VulnNodeCellRender;
import com.intellij.ui.components.JBScrollPane;

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

    private JTree _tree;
    private DefaultMutableTreeNode root;
    private TreePath rootPath;

    private Set<TreePath> expandedTreePaths = new HashSet<>();

    private ArrayList<TreePath> expandedTreePathList;
    private ArrayList<String> expandedNodeNameList;

    public TreeStuff() {

        initData();
        setSize(600,600);
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(getTreePanel(), BorderLayout.CENTER);
        panel.add(getControlsPanel(), BorderLayout.SOUTH);

        add(panel);
    }

    private void buildTree() {
        _tree = new JTree();

        _tree.setRootVisible(true);

        root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);

        //createCategoryNodes(root,1234);

        createCategoryNodesInModel(model,1234);

        _tree.setModel(model);
        _tree.setCellRenderer(new VulnNodeCellRender());
        rootPath = new TreePath(root);

        _tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                expandedTreePaths.add(event.getPath());
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                expandedTreePaths.remove(event.getPath());
            }
        });

//        model.addTreeModelListener(new TreeModelListener() {
//            @Override
//            public void treeNodesChanged(TreeModelEvent e) {
//                System.out.println("treeNodesChanged "+e.getTreePath().getLastPathComponent());
//            }
//
//            @Override
//            public void treeNodesInserted(TreeModelEvent e) {
//                System.out.println("treeNodesInserted "+e.getTreePath().getLastPathComponent());
//
//            }
//
//            @Override
//            public void treeNodesRemoved(TreeModelEvent e) {
//                System.out.println("treeNodesRemoved "+e.getTreePath().getLastPathComponent());
//
//            }
//
//            @Override
//            public void treeStructureChanged(TreeModelEvent e) {
//                System.out.println("treeStructureChanged "+e.getTreePath().getLastPathComponent());
//
//            }
//        });

        _tree.setComponentPopupMenu(new TreePopUpMenu());
    }

    private Component getTreePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5,5,5,5));

        buildTree();
        panel.add(new JBScrollPane(_tree), BorderLayout.CENTER);

        return panel;
    }

    private void saveExpandedNodes() {
        expandedTreePathList = Collections.list(_tree.getExpandedDescendants(rootPath));
        System.out.println("Saved "+expandedTreePathList.size()+" expanded nodes");
    }

    private void openSavedNodes() {
        if(expandedTreePathList!=null && expandedTreePathList.size()>0) {
            System.out.println("Opening "+expandedTreePathList.size()+" saved nodes");
            for(TreePath tp:expandedTreePathList) {
                _tree.expandPath(tp);
            }
        }
        else {
            System.out.println("No opened nodes were saved");
        }
    }

    class TreePopUpMenu extends JPopupMenu {
        public TreePopUpMenu() {
            JMenuItem expandAll = new JMenuItem("Expand all");
            expandAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(int i=0; i<_tree.getRowCount(); i++) {
                        _tree.expandRow(i);
                    }
                }
            });
            add(expandAll);

            JMenuItem collapseAll = new JMenuItem("Collapse all");
            collapseAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(int i=_tree.getRowCount(); i>1; i--) {
                        _tree.collapseRow(i);
                    }
                }
            });
            add(collapseAll);

            add(new JSeparator());

            JMenuItem saveExpandedNodes = new JMenuItem("Save expanded nodes");
            saveExpandedNodes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveExpandedNodes();
                }
            });
            add(saveExpandedNodes);

            JMenuItem openSavedNodes = new JMenuItem("Reopen saved nodes");
            openSavedNodes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openSavedNodes();
                }
            });
            add(openSavedNodes);

            JMenuItem validateSaved = new JMenuItem("Validate saved nodes against new model");
            validateSaved.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(_tree, "Checking to see if treePaths match current model...");
                }
            });
            add(validateSaved);

            JMenuItem search = new JMenuItem("Search");
            search.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inputVal = JOptionPane.showInputDialog("Search for", "test");
                    System.out.println(inputVal);
                }
            });
            add(search);

            add(new JSeparator());

            add(new JMenuItem("Refresh"));

            JMenuItem rebuildTreeNew = new JMenuItem("Rebuild Tree using new model");
            rebuildTreeNew.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Rebuilding tree w/ new model!");
                            DefaultMutableTreeNode root = (DefaultMutableTreeNode)_tree.getModel().getRoot();
                            DefaultTreeModel model = new DefaultTreeModel(root);
                            _tree.setModel(model);
                            root.removeAllChildren();
                            //createCategoryNodes(root,1234);
                            createCategoryNodesInModel(model, 8888);
                            model.reload();
                        }
                    });
                }
            });
            add(rebuildTreeNew);

            JMenuItem rebuildTreeRemove = new JMenuItem("Rebuild Tree remove only");
            rebuildTreeRemove.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Rebuilding tree (removing nodes only)");
                            DefaultTreeModel _treeModel = (DefaultTreeModel)_tree.getModel();
                            DefaultMutableTreeNode root = (DefaultMutableTreeNode)_treeModel.getRoot();
                            //root.removeAllChildren();
                            //createCategoryNodes(root,1234);
                            createCategoryNodesInModel(_treeModel, 8888);
                            _treeModel.reload();
                        }
                    });
                }
            });
            add(rebuildTreeRemove);

            JMenuItem deleteModelNodes = new JMenuItem("Delete all nodes (null out model)");
            deleteModelNodes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            _tree.setModel(null);
                        }
                    });
                }
            });
            add(deleteModelNodes);

            add(new JSeparator());

            JMenuItem insertNode = new JMenuItem("Insert node");
            insertNode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String nodeLabel = JOptionPane.showInputDialog("Enter label");
                    if(nodeLabel != null) {
                        DefaultMutableTreeNode node;
                        DefaultTreeModel model = (DefaultTreeModel) (_tree.getModel());
                        TreePath[] paths = _tree.getSelectionPaths();
                        DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
                        System.out.println("Added node '"+nodeLabel+"' to node "+lastPathComponent);
                    }
                }
            });
            add(insertNode);

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

        JButton deleteButton = new JButton("Delete Node");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode node;
                DefaultTreeModel model = (DefaultTreeModel) (_tree.getModel());
                TreePath[] paths = _tree.getSelectionPaths();
                for (int i = 0; i < paths.length; i++) {
                    node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
                    model.removeNodeFromParent(node);
                }
            }
        });

        JButton infoButton = new JButton("Info");
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTreeInfo();
            }
        });

        panel.add(deleteButton);
        panel.add(infoButton);

        return panel;
    }

    private void showTreeInfo() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) _tree.getModel().getRoot();
        printTreeNode(root);
    }

    private void printTreeNode(DefaultMutableTreeNode node) {
        int childCount = node.getChildCount();

        System.out.println("---" + node.toString() + "---");

        for (int i = 0; i < childCount; i++) {

            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (childNode.getChildCount() > 0) {
                printTreeNode(childNode);
            } else {
                System.out.println(childNode.toString());
            }

        }

        System.out.println("+++" + node.toString() + "+++");
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

    private String formatNodeLabel(String string, int size) {
        return string +" ("+String.valueOf(size)+")";
    }

    private HashMap<String, ArrayList> generateIssues(int maxIssues, int exactNumIssues) {
        ArrayList<String> traceNodes;
        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");

        HashMap<String, ArrayList> issues;
        Random rand = new Random();
        int numIssues = 0;
        if(exactNumIssues<0) {
            numIssues = rand.nextInt(maxIssues) + 1;
        }
        else {
            numIssues = exactNumIssues;
        }

        issues = new HashMap<>();
        issues.put("Exec.java:103", traceNodes);
        for(int i=1; i<numIssues; i++) {
            issues.put("Exec.java:"+i, traceNodes);
        }
        return issues;
    }

    private void createCategoryNodesInModel(DefaultTreeModel model, int hashCode) {
        HashMap<String, ArrayList> issues = generateIssues(4, -1);
        DefaultMutableTreeNode groupingNodeAndChildren = createGroupingNodeAndChildren(formatNodeLabel("Cookie Security: Cookie not sent over SSL", issues.size()), issues);
        model.insertNodeInto(groupingNodeAndChildren,(DefaultMutableTreeNode)model.getRoot(),0);

        issues = generateIssues(10,-1);
        groupingNodeAndChildren = createGroupingNodeAndChildren(formatNodeLabel("Cross-Site Request Forgery", issues.size()), issues);
        model.insertNodeInto(groupingNodeAndChildren,(DefaultMutableTreeNode)model.getRoot(),0);

        issues = generateIssues(-1,50);
        groupingNodeAndChildren = createGroupingNodeAndChildren(formatNodeLabel("Password Management: Password in Configuration File", issues.size()), issues);
        model.insertNodeInto(groupingNodeAndChildren,(DefaultMutableTreeNode)model.getRoot(),0);

        issues = generateIssues(7,-1);
        groupingNodeAndChildren = createGroupingNodeAndChildren(formatNodeLabel("File Disclosure: J2EE", issues.size()), issues);
        model.insertNodeInto(groupingNodeAndChildren,(DefaultMutableTreeNode)model.getRoot(),0);

        issues = generateIssues(-1,66);
        groupingNodeAndChildren = createGroupingNodeAndChildren(formatNodeLabel("SQL Injection", issues.size()), issues);
        model.insertNodeInto(groupingNodeAndChildren,(DefaultMutableTreeNode)model.getRoot(),0);

    }

    private DefaultMutableTreeNode createCategoryNode(GroupTreeItem categoryItem) {
        final DefaultMutableTreeNode groupingNode = new DefaultMutableTreeNode(categoryItem);
        return groupingNode;
    }

    private DefaultMutableTreeNode createGroupingNodeAndChildren(String groupingNodeName, HashMap<String, ArrayList> issues) {
        final int batchSize = 12;
        final DefaultMutableTreeNode groupingNode = new DefaultMutableTreeNode(groupingNodeName);
        groupingNode.setUserObject(new GroupTreeItem(groupingNodeName, groupingNodeName));

        int numIssues = issues.size();
        if(numIssues<=batchSize) {
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
        }
        else {
            // not precise... doesn't matter!
            int numPages = numIssues/batchSize;
            int currentPage = 1;
            DefaultMutableTreeNode pagingNode = null;
            for(String key:issues.keySet()) {
                ArrayList traces = issues.get(key);
                String issueName = key;
                if(pagingNode == null) {
                    pagingNode = new DefaultMutableTreeNode("["+String.valueOf(currentPage)+"-"+String.valueOf(50)+"]");;
                }
                else {
                    DefaultMutableTreeNode issueNode = new DefaultMutableTreeNode(issueName);
                    issueNode.setUserObject(new IssueTreeItem(issueName, issueName));
                    for(int i=0; i<traces.size(); i++) {
                        issueNode.add(new DefaultMutableTreeNode(traces.get(i)));
                    }
                    if(pagingNode.getChildCount()<batchSize) {
                        pagingNode.add(issueNode);
                    }
                    else {
                        pagingNode.add(issueNode);
                        groupingNode.add(pagingNode);
                        pagingNode = null;
                        currentPage++;
                    }
                }
            }
        }

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
