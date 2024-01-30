package com.dse.winams;

import com.dse.parser.object.IVariableNode;

public class VariableTreeNode extends AliasNode implements IVariableTreeNode {

    private IVariableNode variable;

    public VariableTreeNode(IVariableNode v) {
        this.variable = v;
    }

    @Override
    public IVariableNode getVariable() {
        return variable;
    }

    @Override
    public void setVariable(IVariableNode variable) {
        this.variable = variable;
    }
}
