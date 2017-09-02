package com.fortify.fod.remediation.custom;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.GroupedElementsRenderer;

import javax.swing.*;

/**
 * Created by jazl on 9/2/2017.
 */
public class GroupTreeItem extends TreeNodeBase {

    private String id;
    private String label;

    public GroupTreeItem(TreeNodeUserObject object) {
        super(object);
    }

    public GroupTreeItem(String id, String label) {
        super(null);
        this.id = id;
        this.label = label;
    }

    @Override
    public String getId() {
        return "group::"+id;
    }

    @Override
    public String getLabel() {
        return "group::"+label;
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/folder-yellow.png");
    }

    @Override
    public Icon getOpenIcon() {
        return getIcon();
    }

    @Override
    public Icon getClosedIcon() {
        return getIcon();
    }

    @Override
    public String toString() {
        return getLabel();
    }

}
