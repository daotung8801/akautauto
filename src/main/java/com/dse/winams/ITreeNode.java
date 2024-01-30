package com.dse.winams;

import java.util.List;

public interface ITreeNode {
    String getTitle();
    void setTitle(String title);
    List<ITreeNode> getChildren();
}
