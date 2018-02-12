package com.fortify.fod.remediation.demo;

import java.awt.*;
import java.time.LocalTime;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

public class TreeChangesStaticData
{
    private JTree getTree()
    {
        DefaultMutableTreeNode
                root = new DefaultMutableTreeNode("root"),
                leaf;
        DefaultMutableTreeNode[]
                nodes    = new DefaultMutableTreeNode[3],
                subNodes = new DefaultMutableTreeNode[8];
        int[][] keys = {  { 1, 2, 3 }, { 3 }, { 1, 2, 3, 4 }  };
        int subIndex = 0;

        DefaultMutableTreeNode specialNode = new DefaultMutableTreeNode("Special");
        specialNode.add(new DefaultMutableTreeNode("special child 1"));
        specialNode.add(new DefaultMutableTreeNode("special child 2"));
        specialNode.add(new DefaultMutableTreeNode("special child 3"));
        root.add(specialNode);

//        for(int j = 0; j < nodes.length; j++)
//        {
//            nodes[j] = new DefaultMutableTreeNode("node " + (j+1));
//            root.insert(nodes[j], j);
//            for(int k = 0; k < keys[j].length; k++, subIndex++)
//            {
//                String id = "sub " + (j+1) + (k+1);
//                subNodes[subIndex] = new DefaultMutableTreeNode(id);
//                nodes[j].insert(subNodes[subIndex], k);
//                for(int m = 0; m < keys[j][k]; m++)
//                {
//                    id = "leaf " + (j+1) + (k+1) + (m+1);
//                    leaf = new DefaultMutableTreeNode(id);
//                    subNodes[subIndex].insert(leaf, m);
//                }
//            }
//        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);
        expandTree(tree);
        return tree;
    }

    private void expandTree(JTree tree)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        while(e.hasMoreElements())
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            if(node.isLeaf()) break;
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }

    public static void main(String[] args)
    {
        TreeChangesStaticData tc = new TreeChangesStaticData();
        JTree tree = tc.getTree();
        TreeChangerStatic changer = new TreeChangerStatic(tree);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new JScrollPane(tree));
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
        changer.start();
    }
}

class TreeChangerStatic implements Runnable
{
    JTree tree;
    Random seed;
    Thread thread;
    boolean mutate;
    static int changeCnt = 1;

    public TreeChangerStatic(JTree tree)
    {
        this.tree = tree;
        seed = new Random();
    }

    public void run()
    {
        while(mutate)
        {
            try
            {
                Thread.sleep(3000);
            }
            catch(InterruptedException ie)
            {
                System.err.println("interrupt: " + ie.getMessage());
            }
            System.out.println("Making random changes!");

            Enumeration<TreePath> expandedDescendants = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
            ArrayList<TreePath> expandedList = Collections.list(tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot())));


            DefaultMutableTreeNode node = getRandomNode();
            String data = getRandomData();
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

            DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
            //root.removeAllChildren();

            Enumeration e = root.breadthFirstEnumeration();
            while(e.hasMoreElements())
            {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
                if(n.getUserObject().equals("Special")) {
                    n.removeAllChildren();
                    n.add(new DefaultMutableTreeNode("special child 1"));
                    n.add(new DefaultMutableTreeNode("special child 2"));
                    n.add(new DefaultMutableTreeNode("special child 3"));
                    for(int i=0; i<changeCnt; i++) {
                        n.add(new DefaultMutableTreeNode("special child "+3+changeCnt));
                    }
                    changeCnt++;
                    break;
                }
            }

            DefaultMutableTreeNode specialNode = new DefaultMutableTreeNode("Special added at "+ LocalTime.now());
            specialNode.add(new DefaultMutableTreeNode("special child 1"));
            specialNode.add(new DefaultMutableTreeNode("special child 2"));
            specialNode.add(new DefaultMutableTreeNode("special child 3"));
            root.add(specialNode);

            model.valueForPathChanged(new TreePath(node), data);
            model.reload();

//            System.out.println("After reload...reopening nodes");
//            while(expandedDescendants.hasMoreElements()) {
//                TreePath path = expandedDescendants.nextElement();
//                System.out.println("path = "+path);
//                tree.expandPath(path);
//            }

            System.out.println("After reload...reopening nodes (arrayList)");
            for(TreePath path:expandedList) {
                System.out.println("path = "+path);
                tree.expandPath(path);
            }
        }
    }

    private DefaultMutableTreeNode getRandomNode()
    {
        DefaultMutableTreeNode root =
                (DefaultMutableTreeNode)tree.getModel().getRoot();
        // count the nodes in this tree
        Enumeration e = root.breadthFirstEnumeration();
        int count = 0;
        while(e.hasMoreElements())
        {
            e.nextElement();
            count++;
        }
        // pick a random node index, exclusive of the root node [index:0]
        int index = 1 + seed.nextInt(count-1);
        // find the node at the random index of the Enumeration
        e = root.breadthFirstEnumeration();
        count = 0;
        while(e.hasMoreElements())
        {
            Object o = e.nextElement();
            if(count++ == index)
                return (DefaultMutableTreeNode)o;
        }
        return null;
    }

    private String getRandomData()
    {
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        int len = 5;
        char[] chars = new char[len];
        for(int j = 0; j < len; j++)
            chars[j] = alpha.charAt(seed.nextInt(alpha.length()));
        return String.valueOf(chars);
    }

    public void start()
    {
        mutate = true;
        thread = new Thread(this);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }
}