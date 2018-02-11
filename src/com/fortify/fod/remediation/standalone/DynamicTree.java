package com.fortify.fod.remediation.standalone;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class DynamicTree extends JPanel {
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    public DynamicTree() {
        super(new GridLayout(1,0));

        rootNode = new DefaultMutableTreeNode("Root Node @ "+ LocalDateTime.now());
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(new MyTreeModelListener());
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        populateTreeModel();

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    public void createNewModel() {
        treeModel = new DefaultTreeModel(rootNode);
        rootNode.removeAllChildren();

        populateTreeModel();

        treeModel.insertNodeInto(new DefaultMutableTreeNode("new node 1 @ "+LocalDateTime.now()),rootNode,0);
        treeModel.insertNodeInto(new DefaultMutableTreeNode("new node 2 @ "+LocalDateTime.now()),rootNode,1);
        treeModel.insertNodeInto(new DefaultMutableTreeNode("new node 3 @ "+LocalDateTime.now()),rootNode,2);
        tree.setModel(treeModel);
        treeModel.reload();
    }

    public void populateTreeModel() {
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        LocalDateTime now = LocalDateTime.now();
        String p1Name = new String("Parent 1 @ "+now);
        String p2Name = new String("Parent 2 @ "+now);
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