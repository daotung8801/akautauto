package com.dse.winams;

import com.dse.parser.dependency.FunctionCallDependency;
import com.dse.parser.funcdetail.FunctionDetailTree;
import com.dse.parser.object.ICommonFunctionNode;
import com.dse.parser.object.IFunctionNode;
import com.dse.parser.object.INode;
import com.dse.parser.object.IVariableNode;
import com.dse.search.Search2;
import com.dse.testdata.DataTree;
import com.dse.testdata.gen.module.subtree.InitialStubTreeGen;
import com.dse.testdata.object.GlobalRootDataNode;
import com.dse.testdata.object.IDataNode;
import com.dse.testdata.object.RootDataNode;
import com.dse.testdata.object.SubprogramNode;
import com.dse.testdata.object.ValueDataNode;
import com.dse.util.NodeType;
import com.dse.winams.IEntry.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VariableTree extends TreeNode {

    private final IFunctionNode fn;

    public VariableTree(IFunctionNode fn) {
        this.fn = fn;
        setTitle(fn.getSingleSimpleName());
        generateTree();
    }

    private void generateTree() {
        FunctionDetailTree functionDetailTree = new FunctionDetailTree(fn);
        DataTree dataTree = new DataTree(functionDetailTree);
        RootDataNode dataRoot = dataTree.getRoot();

        for (Type type : Type.values()) {
            LabelTreeNode child = new LabelTreeNode(type);
            getChildren().add(child);

            // TODO
            String titlePrefix = "";

            switch (type) {
                case GLOBAL:
                    GlobalRootDataNode globalDataRoot = Search2.findGlobalRoot(dataRoot);
                    List<IDataNode> usedDataNodes = globalDataRoot.getChildren().stream()
                        .filter(n -> n instanceof ValueDataNode)
                        .map(n -> (ValueDataNode) n)
                        .filter(n -> globalDataRoot.isRelatedVariable(n.getCorrespondingVar()))
                        .collect(Collectors.toList());
                    addChildren(child, titlePrefix, usedDataNodes);
                    break;

                case PARAMETER:
                    List<IDataNode> parameterDataNodes = Search2.findArgumentNodes(dataRoot);
                    addChildren(child, titlePrefix, parameterDataNodes);
                    break;

                case RETURN:
                    ValueDataNode returnDataNode = Search2.findReturnNode(dataRoot);
                    addChildren(child, titlePrefix, Collections.singletonList(returnDataNode));
                    break;

                case STATIC:
                    RootDataNode staticDataRoot = Search2.findStaticRoot(dataRoot);
                    addChildren(child, titlePrefix, staticDataRoot.getChildren());
                    break;

                case FUNCTION_CALL:
                    List<INode> calledFunctions = new ArrayList<>();
                    fn.getDependencies().forEach(d -> {
                        if (d instanceof FunctionCallDependency) {
                            if (d.getStartArrow().equals(fn)) {
                                calledFunctions.add(d.getEndArrow());
                            }
                        }
                    });

                    List<SubprogramNode> stubDataNodes = Search2.searchStubableSubprograms(dataRoot);
                    stubDataNodes.removeIf(n -> !calledFunctions.contains(n.getFunctionNode()));
                    stubDataNodes.forEach(n -> {
                        try {
                            new InitialStubTreeGen().addSubprogram(n);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    addChildren(child, titlePrefix, stubDataNodes);
                    break;
            }
        }

    }

    private void addChildren(ITreeNode parent, String prefix, Collection<? extends IDataNode> dataNodes) {
        for (IDataNode child : dataNodes) {
            IAliasNode node = null;
            String title = null;

            if (child instanceof SubprogramNode) {
                SubprogramNode subprogramNode = (SubprogramNode) child;
                if (subprogramNode.getFunctionNode() instanceof ICommonFunctionNode) {
                    ICommonFunctionNode functionNode = (ICommonFunctionNode) subprogramNode.getFunctionNode();
                    node = new FunctionTreeNode(functionNode);
                    // TODO
                    title = prefix + functionNode.getSingleSimpleName();
                }
            } else if (child instanceof ValueDataNode) {
                ValueDataNode valueDataNode = (ValueDataNode) child;
                IVariableNode variableNode = valueDataNode.getCorrespondingVar();
                node = new VariableTreeNode(variableNode);
                // TODO
                title = prefix + valueDataNode.getVituralName();
            }

            if (node != null) {
                String[] chainNames = retrieveChainNames(child);
                node.setChainNames(chainNames);
                node.setTitle(title);
                parent.getChildren().add(node);
                addChildren(node, "", child.getChildren());
            }
        }
    }

    private static String[] retrieveChainNames(IDataNode node) {
        if (node instanceof RootDataNode && ((RootDataNode) node).getLevel() == NodeType.ROOT)
            return new String[] {node.getName()};

        String[] parentChainNames = retrieveChainNames(node.getParent());
        int parentChainLength = parentChainNames.length;
        String[] chainNames = new String[parentChainLength + 1];
        System.arraycopy(parentChainNames, 0, chainNames, 0, parentChainLength);
        chainNames[parentChainLength] = node.getName();
        return chainNames;
    }

    public IFunctionNode getFunction() {
        return fn;
    }
}