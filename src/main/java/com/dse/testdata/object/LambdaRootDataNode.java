package com.dse.testdata.object;

import com.dse.parser.externalvariable.RelatedExternalVariableDetecter;
import com.dse.parser.object.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dse.util.NodeType.LAMBDA;

public class LambdaRootDataNode extends RootDataNode {

    // map input to expected output of lambda variables
    // only used for LAMBDA level
    private Map<ValueDataNode, ValueDataNode> lambdaInputExpOutputMap;

    private boolean hide = true;

    private List<IVariableNode> relatedVariables;

    private StructureNode structureNode;

    public LambdaRootDataNode() {
        level = LAMBDA;
        lambdaInputExpOutputMap = new HashMap<>();
    }

    public void setFunctionNode(ICommonFunctionNode functionNode) {
        super.setFunctionNode(functionNode);

        if (functionNode instanceof MacroFunctionNode)
            functionNode = ((MacroFunctionNode) functionNode).getCorrespondingFunctionNode();

        RelatedExternalVariableDetecter detector = new RelatedExternalVariableDetecter((IFunctionNode) functionNode);
        relatedVariables = detector.findVariables();

        INode parent = ((IFunctionNode) functionNode).getRealParent();
        if (parent == null)
            parent = functionNode.getParent();

        if (parent instanceof StructureNode) {
            this.structureNode = (StructureNode) parent;
        }
    }

    public boolean isRelatedVariable(IVariableNode v) {
        if (structureNode != null && v instanceof InstanceVariableNode) {
            if (v.resolveCoreType() == structureNode)
                return true;
        }

        if (relatedVariables != null)
            return relatedVariables.contains(v);
        else
            return false;
    }

    public Map<ValueDataNode, ValueDataNode> getLambdaInputExpOutputMap() {
        return lambdaInputExpOutputMap;
    }

    public void setLambdaInputExpOutputMap(Map<ValueDataNode, ValueDataNode> lambdaInputExpOutputMap) {
        this.lambdaInputExpOutputMap = lambdaInputExpOutputMap;
    }

    public boolean putLambdaExpectedOutput(ValueDataNode expectedOuput) {
        ValueDataNode input = null;
        for (IDataNode child : getChildren()) {
            if (((ValueDataNode) child).getCorrespondingVar().getAbsolutePath().equals(expectedOuput.getCorrespondingVar().getAbsolutePath())) {
                input = (ValueDataNode) child;
                break;
            }
        }

        if (input != null) {
            lambdaInputExpOutputMap.remove(input);
            lambdaInputExpOutputMap.put(input, expectedOuput);
            return true;
        }

        return false;
    }

    public boolean isShowRelated() {
        return hide;
    }

    public void setShowRelated(boolean hide) {
        this.hide = hide;
    }

    public List<IVariableNode> getRelatedVariables() {
        return relatedVariables;
    }
}
