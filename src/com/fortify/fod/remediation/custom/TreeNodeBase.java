package com.fortify.fod.remediation.custom;


public abstract class TreeNodeBase {
    protected TreeNodeUserObject userObject;
    public TreeNodeBase(TreeNodeUserObject object) {
        userObject = object;
    }

    public abstract String getId();
    public abstract String getLabel();
}
