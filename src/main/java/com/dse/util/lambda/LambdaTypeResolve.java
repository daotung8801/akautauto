package com.dse.util.lambda;

import com.dse.parser.AllVariablesParser;
import com.dse.parser.DeclaredVariableParser;
import com.dse.parser.FunctionCallParser;
import com.dse.parser.ReturnStatementParser;
import com.dse.parser.dependency.Dependency;
import com.dse.parser.dependency.FunctionCallDependency;
import com.dse.parser.dependency.finder.MethodFinder;
import com.dse.parser.object.*;
import com.dse.util.Utils;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTEqualsInitializer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import java.io.File;
import java.util.*;

public class LambdaTypeResolve {

    public static ArrayList<IASTReturnStatement> getAllReturnStatements(LambdaFunctionNode lambdaFunctionNode) {
        ICPPASTLambdaExpression fnAST = lambdaFunctionNode.getOriginalAST();
        ReturnStatementParser visitor = new ReturnStatementParser();
        fnAST.accept(visitor);
        return visitor.getReturnStatements();
    }

    public static String getReturnTypeByFunctionCall(LambdaFunctionNode lambdaFunctionNode) {
        ICPPASTLambdaExpression fnLambdaAST = lambdaFunctionNode.getOriginalAST();
        if (fnLambdaAST.getParent() instanceof CPPASTEqualsInitializer) {
            if (fnLambdaAST.getParent().getParent() instanceof CPPASTDeclarator) {
                String fnLambdaName = fnLambdaAST.getParent().getParent().getChildren()[0].getRawSignature();
                IASTFunctionDefinition fnAST = getFunctionContainLambdaExpression(fnLambdaAST);
                FunctionCallParser visitor = new FunctionCallParser();
                fnAST.accept(visitor);
                for (IASTFunctionCallExpression e : visitor.getExpressions()) {
                    if (e.getChildren()[0].getRawSignature().equals(fnLambdaName)) {
                        // lambda function is an argument
                        if (e.getParent() instanceof CPPASTFunctionCallExpression) {
                            CPPASTFunctionCallExpression functionCallExpression = (CPPASTFunctionCallExpression) e.getParent();
                            IFunctionNode functionNode = (IFunctionNode) new MethodFinder(lambdaFunctionNode).find(functionCallExpression);
                            IASTInitializerClause[] args = functionCallExpression.getArguments();
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] instanceof IASTFunctionCallExpression
                                        && ((IASTFunctionCallExpression) args[i]).getFunctionNameExpression().getRawSignature().equals(fnLambdaName)) {
                                    return functionNode.getArguments().get(i).getRawType();
                                }
                            }
                        } else {
                            IASTNode node = e.getParent();
                            while (true) {
                                if (node instanceof CPPASTSimpleDeclaration || node instanceof IASTFunctionDefinition) {
                                    break;
                                }
                                node = node.getParent();
                            }
                            if (node instanceof CPPASTSimpleDeclaration) {
                                return ((CPPASTSimpleDeclaration) node).getDeclSpecifier().getRawSignature();
                            }
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }

    private static IASTFunctionDefinition getFunctionContainLambdaExpression(IASTNode node) {
        IASTNode parent = node.getParent();
        while (true) {
            if (parent instanceof IASTFunctionDefinition) {
                return (IASTFunctionDefinition) parent;
            }
            parent = parent.getParent();
        }
    }

    public static ArrayList<IASTIdExpression> getAllVariables(LambdaFunctionNode lambdaFunctionNode) {
        /*
        list has duplicates
         */
        ArrayList<IASTIdExpression> allVariables = new ArrayList<>();
        IASTFunctionDefinition fnAST = lambdaFunctionNode.getAST();
        AllVariablesParser visitor = new AllVariablesParser();
        fnAST.accept(visitor);
        return visitor.getIdExpressions();
    }


    public static List<IASTNode> getDeclaredVariables(LambdaFunctionNode lambdaFunctionNode) {

        ArrayList<IASTNode> declaredVariables = new ArrayList<>();
        IASTFunctionDefinition fnAST = lambdaFunctionNode.getAST();
        DeclaredVariableParser visitor = new DeclaredVariableParser();
        fnAST.accept(visitor);
        return visitor.getVariables();
    }

    //    public static Map<String, String> getUndefinedVariables(LambdaFunctionNode lambdaFunctionNode) {
//        List<IASTNode> declaredVariables = getDeclaredVariables(lambdaFunctionNode);
//        List<IASTIdExpression> allVariables = getAllVariables(lambdaFunctionNode);
//        Map<String, String> mapVariables = new HashMap<String, String>();
//
//        for (IASTIdExpression v :
//                allVariables) {
//            mapVariables.put(v.getRawSignature(), "unknown");
//        }
//        ArrayList <String> datatype_varname = new ArrayList<>();
//
//        for (IASTNode v :
//                declaredVariables) {
//            String datatype_varname_ = v.getRawSignature();
//            String [] arr = datatype_varname_.split(" ", -1);
//            mapVariables.put(arr[1], arr[0]);
//        }
//        return mapVariables;
//    }
    public static Set<String> getUndefinedVariables(LambdaFunctionNode lambdaFunctionNode) {
        Set<String> allVarsString = new HashSet<String>();
        Set<String> declaredVarsString = new HashSet<String>();
        List<IASTNode> declaredVariables = getDeclaredVariables(lambdaFunctionNode);
        List<IASTIdExpression> allVariables = getAllVariables(lambdaFunctionNode);

        for (IASTIdExpression v :
                allVariables) {
            String vString = v.getRawSignature();
            allVarsString.add(vString);
        }

        for (IASTNode variable :
                declaredVariables) {
            /**
             * Declaration in Lambda
             */
            if (variable instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) variable;

                for (IASTDeclarator declarator : declaration.getDeclarators()) {
                    String variableName = declarator
                            .getName()
                            .getRawSignature();
                    declaredVarsString.add(variableName);
                }
                /**
                 * Declaration as Parameter
                 */
            } else if (variable instanceof IASTParameterDeclaration) {
                IASTParameterDeclaration declaration = (IASTParameterDeclaration) variable;
                IASTDeclarator declarator = declaration.getDeclarator();
                String variableName = declarator.getName().getRawSignature();
                declaredVarsString.add(variableName);
            }
        }
        Set<String> undefinedVarsString = new HashSet<String>();
        allVarsString.removeAll(declaredVarsString);
        undefinedVarsString = allVarsString;
        return undefinedVarsString;
    }

    public static Map<String, String> getUndefinedVariablesType(LambdaFunctionNode lambdaFunctionNode) {
        Map<String, String> functionCallLambdaVariables = new HashMap<>();
        Map<String, String> classAttributes = new HashMap<>();
        Map<String, String> globalVaribales = new HashMap<>();

        /**
         * 1. Declaration in Function Call Lambda (Param + Simple)
         * 2. Attribute of Class
         * 3. Global Variable in same file
         * 4. Variable is Global Variable in another file
         */
        Set<String> undefinedVariables = getUndefinedVariables(lambdaFunctionNode);
        Map<String, String> undefinedVariablesType = new HashMap<>();

        List<IASTNode> declaredVariables = new ArrayList<>();
        for (Dependency d : lambdaFunctionNode.getDependencies()) {
            if (d instanceof FunctionCallDependency) {
                FunctionCallDependency functionCallDependency = (FunctionCallDependency) d;

                IFunctionNode fn = (IFunctionNode) functionCallDependency.getStartArrow();

                IASTFunctionDefinition fnd = fn.getAST();

                DeclaredVariableParser visitor = new DeclaredVariableParser();
                fnd.accept(visitor);
                declaredVariables = visitor.getVariables();

                /**
                 * 1. Declaration in Function call Lambda
                 */
                for (IASTNode variable : declaredVariables) {
                    /**
                     * Declaration in Function call Lambda
                     */
                    if (variable instanceof IASTSimpleDeclaration) {
                        IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) variable;
                        String name = "";
                        for (IASTDeclarator declarator : declaration.getDeclarators()) {
                            name = declarator
                                    .getName()
                                    .getRawSignature();
                        }

                        String datatype_varname = declaration.getRawSignature(); // arr[1] = name, arr[0] = type
                        String[] arr = datatype_varname.split(" ", -1);
                        String type = arr[0];

                        if (undefinedVariables.contains(name)) {
                            undefinedVariables.remove(name);
                            undefinedVariablesType.put(name, type);

                            // classify
                            functionCallLambdaVariables.put(name, type);
                        }

                    }
                    /**
                     * Declaration as Parameter in Function call Lambda
                     */
                    else if (variable instanceof IASTParameterDeclaration) {
                        IASTParameterDeclaration declaration = (IASTParameterDeclaration) variable;

                        IASTDeclarator declarator = declaration.getDeclarator();
                        String name = declarator.getName().getRawSignature();

                        String datatype_varname = declaration.getRawSignature(); // arr[1] = name, arr[0] = type
                        String[] arr = datatype_varname.split(" ", -1);

                        String type = arr[0];

                        if (undefinedVariables.contains(name)) {
                            undefinedVariables.remove(name);
                            undefinedVariablesType.put(name, type);

                            // classify
                            functionCallLambdaVariables.put(name, type);
                        }
                    }
                }
                if (undefinedVariables.isEmpty())
                    return undefinedVariablesType;


                /**
                 * 2. Declaration in Class(Class has Function call)
                 if Function Call is Method in Class
                 */
                INode fnParent = fn.getRealParent();

                if (fnParent instanceof ClassNode) {
                    ClassNode classNode = (ClassNode) fnParent;
                    List<IVariableNode> attributes = classNode.getAttributes();
                    for (IVariableNode att : attributes) {
                        /**
                         * Variable is Attribute of Class
                         */
                        String name = att.getName();
                        String type = att.getRealType();
                        if (undefinedVariables.contains(name)) {
                            undefinedVariables.remove(name);
                            undefinedVariablesType.put(name, type);

                            // classify
                            classAttributes.put(name, type);
                        }
                    }
                }

                if (undefinedVariables.isEmpty())
                    return undefinedVariablesType;

                /**
                 *3. Declaration:
                 Variable is Global Variable
                 */
                List<IVariableNode> argumentsAndGlobalVariables = fn.getArgumentsAndGlobalVariables();
                for (IVariableNode var : argumentsAndGlobalVariables) {
                    if (var instanceof ExternalVariableNode) {
                        String name = var.getName();
                        String type = var.getRealType();

                        if (undefinedVariables.contains(name)) {
                            undefinedVariables.remove(name);
                            undefinedVariablesType.put(name, type);

                            // classify
                            globalVaribales.put(name, type);
                        }

                    }
                }
                if (undefinedVariables.isEmpty())
                    return undefinedVariablesType;
            }
        }


//        /**
//         * 4. Declaration: Variable is Global Variable in another file
//         */
//
//        ISourcecodeFileNode sourcecodeFileNode = Utils.getSourcecodeFile(lambdaFunctionNode);
//        if (sourcecodeFileNode != null) {
//            List<Dependency> dependencies = sourcecodeFileNode.getDependencies();
//
//            for (Dependency d : dependencies) {
//
//                if (d instanceof IncludeHeaderDependency && d.getStartArrow().equals(sourcecodeFileNode)) {
//                    INode fileHeader = d.getEndArrow();
//
//                    for (Node n : fileHeader.getChildren()) {
//                        if (n instanceof ExternalVariableNode) {
//                            ExternalVariableNode globalVar = (ExternalVariableNode) n;
//                            String name = globalVar.getName();
//                            String type = globalVar.getRealType();
//
//                            if (undefinedVariables.contains(name)) {
//                                undefinedVariables.remove(name);
//                                undefinedVariablesType.put(name, type);
//                            }
//                        }
//                    }
//
//                    System.out.println();
//                }
//            }
//        }


        return undefinedVariablesType;
    }

    public static Map<String, IASTNode> getUndefinedFunctionCallLambdaVariables(LambdaFunctionNode lambdaFunctionNode) {
        Map<String, IASTNode> functionCallLambdaVariables = new HashMap<>();
        Map<String, String> classAttributes = new HashMap<>();
        Map<String, String> globalVaribales = new HashMap<>();

        /**
         * 1. Declaration in Function Call Lambda (Param + Simple)
         * 2. Attribute of Class
         * 3. Global Variable in same file
         * 4. Variable is Global Variable in another file
         */
        Set<String> undefinedVariables = getUndefinedVariables(lambdaFunctionNode);
        Map<String, String> undefinedVariablesType = new HashMap<>();

        List<IASTNode> declaredVariables = new ArrayList<>();
        for (Dependency d : lambdaFunctionNode.getDependencies()) {
            if (d instanceof FunctionCallDependency) {
                FunctionCallDependency functionCallDependency = (FunctionCallDependency) d;

                IFunctionNode fn = (IFunctionNode) functionCallDependency.getStartArrow();

                IASTFunctionDefinition fnd = fn.getAST();

                DeclaredVariableParser visitor = new DeclaredVariableParser();
                fnd.accept(visitor);
                declaredVariables = visitor.getVariables();

                /**
                 * 1. Declaration in Function call Lambda
                 */
                for (IASTNode variable : declaredVariables) {
                    /**
                     * Declaration in Function call Lambda
                     */
                    if (variable instanceof IASTSimpleDeclaration) {
                        IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) variable;
                        String name = "";
                        for (IASTDeclarator declarator : declaration.getDeclarators()) {
                            name = declarator
                                    .getName()
                                    .getRawSignature();
                        }

                        String datatype_varname = declaration.getRawSignature(); // arr[1] = name, arr[0] = type
                        String[] arr = datatype_varname.split(" ", -1);
                        String type = arr[0];

                        if (undefinedVariables.contains(name)) {
                            undefinedVariables.remove(name);
                            undefinedVariablesType.put(name, type);

                            // classify
                            functionCallLambdaVariables.put(name, declaration);
                        }

                    }
                    /**
                     * Declaration as Parameter in Function call Lambda
                     */
                    else if (variable instanceof IASTParameterDeclaration) {
                        IASTParameterDeclaration declaration = (IASTParameterDeclaration) variable;

                        IASTDeclarator declarator = declaration.getDeclarator();
                        String name = declarator.getName().getRawSignature();

                        String datatype_varname = declaration.getRawSignature(); // arr[1] = name, arr[0] = type
                        String[] arr = datatype_varname.split(" ", -1);

                        String type = arr[0];

                        if (undefinedVariables.contains(name)) {
                            undefinedVariables.remove(name);
                            undefinedVariablesType.put(name, type);

                            // classify
                            functionCallLambdaVariables.put(name, declaration);
                        }
                    }
                }
                if (undefinedVariables.isEmpty())
                    return functionCallLambdaVariables;
            }
        }
        return functionCallLambdaVariables;
    }
}
