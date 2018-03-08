package com.fortify.fod.remediation.demo;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class DynamicTree extends JPanel {
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    TreePath rootTreePath;
    private ArrayList<TreePath> expandedDescendants;

    public void expandTreeBF()
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        while(e.hasMoreElements())
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            System.out.println("Expanding: "+node);
            if(node.isLeaf()) break;
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }

    public DynamicTree() {
        super(new GridLayout(1,0));

        rootNode = new DefaultMutableTreeNode("Root Node @ "+ LocalDateTime.now());
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        rootTreePath = new TreePath(rootNode);

        populateTreeModel();

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    public void saveExpandedNodes() {
        expandedDescendants = Collections.list(tree.getExpandedDescendants(rootTreePath));
    }

    public void restoreExpandedNodes() {
        ArrayList<TreePath> treePaths = new ArrayList<>();
        Enumeration<DefaultMutableTreeNode> e = rootNode.depthFirstEnumeration();
        e.nextElement();
        while(e.hasMoreElements()) {
            DefaultMutableTreeNode e1 = (DefaultMutableTreeNode) e;
            treePaths.add(new TreePath(e1.getPath()));
            e.nextElement();
        }

        for(TreePath t:expandedDescendants) {
            int rowForPath = tree.getRowForPath(t);
            System.out.println("rowForPath "+t+" = "+rowForPath+", hashCode = "+t.hashCode());

            TreePath refPath = tree.getPathForRow(1);
            tree.expandPath(t);

//            tree.expandPath(new TreePath(t.getLastPathComponent()));

//            DefaultMutableTreeNode pathComponent = (DefaultMutableTreeNode) t.getLastPathComponent();
//            TreePath tp = new TreePath(pathComponent.getPath());
//            tree.expandPath(tp);
        }
    }

    public void restoreExpandedNodes2() {
//        for(int i=0; i<tree.getRowCount(); i++) {
//            tree.expandRow(i);
//        }
        //ArrayList<TreePath> expandedDescendantsCurrent = Collections.list(tree.getExpandedDescendants(rootTreePath));

        ArrayList<TreePath> treePaths = new ArrayList<>();
        Enumeration<DefaultMutableTreeNode> e = rootNode.depthFirstEnumeration();
        while(e.hasMoreElements()) {
            DefaultMutableTreeNode e1 = (DefaultMutableTreeNode) e;
            treePaths.add(new TreePath(e1.getPath()));
        }
        for(TreePath t:expandedDescendants) {
            tree.expandPath(t);
        }
    }

    public void createNewModel() {
        //treeModel = new DefaultTreeModel(rootNode);

        //populateTreeModel();
//
//        treeModel.insertNodeInto(new DefaultMutableTreeNode("new node 1 @ "+LocalDateTime.now()),rootNode,0);
//        treeModel.insertNodeInto(new DefaultMutableTreeNode("new node 2 @ "+LocalDateTime.now()),rootNode,1);
//        treeModel.insertNodeInto(new DefaultMutableTreeNode("new node 3 @ "+LocalDateTime.now()),rootNode,2);

        saveExpandedNodes();
        updateTreeModel();

        //treeModel.reload();
        //tree.setModel(treeModel);
        restoreExpandedNodes();

        //treeModel.reload();
        //tree.setModel(treeModel) ;
    }

    public void updateTreeModel() {
        rootNode.removeAllChildren();
        DefaultTreeModel modelToUpdate = (DefaultTreeModel)tree.getModel();
        //((DefaultMutableTreeNode)tree.getModel().getRoot()).removeAllChildren();

        LocalDateTime now = LocalDateTime.now();
        String p1Name = new String("Parent 1");
        String p2Name = new String("Parent 2");
        String c1Name = new String("Child 1 @ "+now);
        String c2Name = new String("Child 2 @ "+now);

        DefaultMutableTreeNode p1, p2, c1, c2;

        p1 = new DefaultMutableTreeNode(p1Name);
        modelToUpdate.insertNodeInto(p1,rootNode, 0);
        p2 = new DefaultMutableTreeNode(p2Name);
        modelToUpdate.insertNodeInto(p2,rootNode, 1);

        c1 = new DefaultMutableTreeNode(c1Name);
        modelToUpdate.insertNodeInto(c1, p1,0);

        c2 = new DefaultMutableTreeNode(c2Name);
        modelToUpdate.insertNodeInto(c2, p2,0);

        modelToUpdate.reload();
    }

    public void populateTreeModel() {
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        LocalDateTime now = LocalDateTime.now();
        String p1Name = new String("Parent 1");
        String p2Name = new String("Parent 2");
        String c1Name = new String("Child 1 @ "+now);
        String c2Name = new String("Child 2 @ "+now);

        DefaultMutableTreeNode p1, p2, c1, c2;

        p1 = new DefaultMutableTreeNode(p1Name);
        model.insertNodeInto(p1,rootNode, 0);
        p2 = new DefaultMutableTreeNode(p2Name);
        model.insertNodeInto(p2,rootNode, 1);

        c1 = new DefaultMutableTreeNode(c1Name);
        model.insertNodeInto(c1, p1,0);

        c2 = new DefaultMutableTreeNode(c2Name);
        model.insertNodeInto(c2, p2,0);

        tree.setModel(model);
        model.reload();
    }

    /** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
        return addObject(parent, child, false);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,Object child,boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    class MyTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode)(node.getChildAt(index));

            System.out.println("The user has finished editing the node.");
            System.out.println("New value: " + node.getUserObject());
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
}