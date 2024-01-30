package com.dse.winams;

import com.dse.parser.object.ICommonFunctionNode;

public class FunctionTreeNode extends AliasNode implements IFunctionTreeNode {

    private ICommonFunctionNode function;

    public FunctionTreeNode(ICommonFunctionNode function) {
        this.function = function;
    }

    @Override
    public ICommonFunctionNode getFunction() {
        return function;
    }

    @Override
    public void setFunction(ICommonFunctionNode function) {
        this.function = function;
    }
}
