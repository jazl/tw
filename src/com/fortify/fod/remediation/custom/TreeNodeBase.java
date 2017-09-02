package com.fortify.fod.remediation.custom;


import javax.swing.*;

public abstract class TreeNodeBase {
    protected TreeNodeUserObject userObject;
    public TreeNodeBase(TreeNodeUserObject object) {
        userObject = object;
    }

    public abstract String getId();
    public abstract String getLabel();
    public abstract Icon getIcon();
    public abstract Icon getOpenIcon();
    public abstract Icon getClosedIcon();
}
