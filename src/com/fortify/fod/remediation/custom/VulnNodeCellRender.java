package com.fortify.fod.remediation.custom;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class VulnNodeCellRender implements TreeCellRenderer {
    JLabel renderer = new JLabel();
    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component returnValue = null;
        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof GroupTreeItem) {
                GroupTreeItem uo = (GroupTreeItem) userObject;
                renderer.setText("::"+uo.getLabel());
                if (selected) {
                    renderer.setBackground(defaultRenderer.getBackgroundSelectionColor());
                } else {
                    renderer.setBackground(defaultRenderer.getBackgroundNonSelectionColor());
                }
                renderer.setEnabled(tree.isEnabled());
                returnValue = renderer;
            }
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }
}