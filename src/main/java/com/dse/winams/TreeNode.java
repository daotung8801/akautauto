package com.dse.winams;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode implements ITreeNode {

    protected String title;

    private final List<ITreeNode> children = new ArrayList<>();

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<ITreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
