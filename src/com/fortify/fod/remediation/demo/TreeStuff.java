package com.fortify.fod.remediation.demo;

import com.fortify.fod.remediation.custom.GroupTreeItem;
import com.fortify.fod.remediation.custom.IssueTreeItem;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
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
        setSize(400,400);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JBScrollPane(getTree()), BorderLayout.CENTER);
        panel.add(getControlsPanel(), BorderLayout.SOUTH);

        add(panel);
    }

    private Component getTree() {
        JPanel panel = new JPanel(new BorderLayout());

        tree = new Tree();

        root = new DefaultMutableTreeNode();
        TreeModel model = new DefaultTreeModel(root);

        createNodes(root,1234);

        tree.setModel(model);

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

        panel.add(new JBScrollPane(tree), BorderLayout.CENTER);

        return panel;
    }

    private Component getControlsPanel() {
        JPanel panel = new JPanel();

        JButton testButton = new JButton("Test");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("openButton clicked, size = "+expandedTreePaths.size());
                expandedDescendants = tree.getExpandedDescendants(rootPath);
            }
        });

        JButton openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (expandedDescendants.hasMoreElements()) {
                    TreePath treePath = expandedDescendants.nextElement();
                    if(treePath != null) {
                        System.out.println("expanding path: "+treePath);
                        tree.expandPath(treePath);
                    }
                }
            }
        });

        panel.add(testButton);
        panel.add(openButton);

        return panel;
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
}
