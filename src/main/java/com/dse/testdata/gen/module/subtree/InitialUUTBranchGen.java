package com.dse.testdata.gen.module.subtree;

import auto_testcase_generation.instrument.FunctionInstrumentationForAllCoverages;
import auto_testcase_generation.pairwise.Value;
import com.dse.environment.Environment;
import com.dse.parser.funcdetail.IFunctionDetailTree;
import com.dse.parser.object.*;
import com.dse.parser.object.INode;
import com.dse.search.Search;
import com.dse.search.condition.FunctionNodeCondition;
import com.dse.testdata.InputCellHandler;
import com.dse.testdata.object.*;
import com.dse.testdata.object.GlobalRootDataNode;
import com.dse.testdata.object.RootDataNode;
import com.dse.util.NodeType;
import com.dse.util.Utils;
import com.dse.util.lambda.LambdaTypeResolve;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import java.util.List;
import java.util.Map;

import static com.dse.util.NodeType.SBF;
import static com.dse.util.NodeType.STATIC;

public class InitialUUTBranchGen extends AbstractInitialTreeGen {
    @Override
    public void generateCompleteTree(RootDataNode root, IFunctionDetailTree functionTree) throws Exception {
        this.root = root;
        functionNode = root.getFunctionNode();
        INode sourceCode = Utils.getSourcecodeFile(functionNode);

        if (sourceCode instanceof ISourcecodeFileNode) {
            UnitNode unitNode = new UnitUnderTestNode(sourceCode);
//            unitNode.setStubChildren(false);

            root.addChild(unitNode);
            unitNode.setParent(root);

            if (Environment.getInstance().getSBFs().contains(sourceCode))
                generateSBFBranch(unitNode, sourceCode, functionNode);

            RootDataNode globalRoot = generateGlobalVarBranch(unitNode, functionTree);

            if (functionNode instanceof LambdaFunctionNode) {
                RootDataNode lambdaRoot = generateLambdaBranch(unitNode, (LambdaFunctionNode) functionNode);
                unitNode.getChildren().add(lambdaRoot);
            }

            IDataNode sut = new InitialArgTreeGen().generate(unitNode, functionNode);

            IDataNode staticRoot = generateStaticBranch(sut, functionNode);
            sut.getChildren().add(0, staticRoot);

            if (functionNode instanceof ConstructorNode) {
                sut.getChildren().clear();
                expandInstance((ConstructorNode) functionNode, globalRoot);
            }
        }
    }

    private RootDataNode generateLambdaBranch(UnitNode unitNode, LambdaFunctionNode lambdaFunctionNode) throws Exception {
        RootDataNode rootDataNode = new LambdaRootDataNode();
//        rootDataNode.setName("<< LAMBDA BRANCH >>");
        Map<String, IASTNode> lambdaFunctionCallVarMap =
                LambdaTypeResolve.getUndefinedFunctionCallLambdaVariables(lambdaFunctionNode);

        for (Map.Entry<String, IASTNode> entry : lambdaFunctionCallVarMap.entrySet()) {
            IASTSimpleDeclaration ast = (IASTSimpleDeclaration) entry.getValue(); // TODO: Ex: x
            VariableNode v = new VariableNode();
            v.setAST(ast);
            v.setParent(functionNode);
            genInitialTree(v, rootDataNode);
        }

        rootDataNode.setParent(unitNode);

        return rootDataNode;
    }

    // case test constructor
    private void expandInstance(IFunctionNode sut, RootDataNode globalRoot) throws Exception {
        INode parent = sut.getRealParent() == null ? sut.getParent() : sut.getRealParent();

        for (IDataNode child : globalRoot.getChildren()) {
            if (child instanceof ValueDataNode) {
                ValueDataNode globalVar = (ValueDataNode) child;

                if (globalVar.getCorrespondingVar() instanceof InstanceVariableNode
                        && globalVar.getCorrespondingType().equals(parent)) {

                    new InputCellHandler().commitEdit(globalVar, globalVar.getCorrespondingType().getName());

                    if (!globalVar.getChildren().isEmpty() && globalVar.getChildren().get(0) instanceof ValueDataNode) {
                        ValueDataNode subclass = (ValueDataNode) globalVar.getChildren().get(0);
                        new InputCellHandler().commitEdit(subclass, sut.getName());
                    }
                }
            }
        }
    }

    private RootDataNode generateSBFBranch(IDataNode current, INode sourceNode, ICommonFunctionNode sut) {
        RootDataNode sbfRoot = new RootDataNode(SBF);

        List<INode> functions = Search.searchNodes(sourceNode, new FunctionNodeCondition());
        List<String> expressions = AbstractInitialTreeGen.filterCalledFunctions(sut);

        for (int i = 0; i < functions.size(); i++) {
            INode func = functions.get(i);
            String funcName = (func instanceof FunctionNode) ? ((FunctionNode) func).getAST().getDeclarator().getName().getRawSignature() : "";
            if (!expressions.contains(funcName)) {
                functions.remove(func);
                i--;
            }
        }

        for (INode child : functions) {
            if (child instanceof FunctionNode && !child.equals(sut)) {
                FunctionNode functionNode = (FunctionNode) child;
                SubprogramNode subprogramNode = new SubprogramNode(functionNode);

                if (functionNode.isTemplate())
                    subprogramNode = new TemplateSubprogramDataNode(functionNode);

                sbfRoot.addChild(subprogramNode);
                subprogramNode.setParent(sbfRoot);
            }
        }

        current.addChild(sbfRoot);
        sbfRoot.setParent(current);

        return sbfRoot;
    }

    private RootDataNode generateStaticBranch(IDataNode current, ICommonFunctionNode sut) throws Exception {
        logger.debug("generateStaticBranch");
        RootDataNode staticRoot = new RootDataNode(STATIC);

        if (FunctionInstrumentationForAllCoverages.STATIC_REFACTOR
            && !functionNode.isTemplate() && !functionNode.isMethod()) {
            List<StaticVariableNode> staticVars = sut.getStaticVariables();

            for (StaticVariableNode staticVar : staticVars) {
                ValueDataNode dataNode = genInitialTree(staticVar, staticRoot);
                dataNode.setExternel(true);
            }
        }

        staticRoot.setParent(current);
        staticRoot.setFunctionNode(functionNode);

        return staticRoot;
    }

    private RootDataNode generateGlobalVarBranch(IDataNode current, IFunctionDetailTree functionTree) throws Exception {
        logger.debug("generateGlobalVarBranch");
        RootDataNode globalVarRoot = new GlobalRootDataNode();

        List<INode> globalElements = functionTree.getSubTreeRoot(NodeType.GLOBAL).getElements();

        for (INode globalElement : globalElements) {
            if (globalElement instanceof VariableNode) {
                VariableNode globalVariable = (VariableNode) globalElement;
                if (!globalVariable.isConst()) {
                    ValueDataNode dataNode = genInitialTree(globalVariable, globalVarRoot);
                    dataNode.setExternel(true);
                }
            }
        }

        current.addChild(globalVarRoot);
        globalVarRoot.setParent(current);
        globalVarRoot.setFunctionNode(functionNode);

        return globalVarRoot;
    }
}
