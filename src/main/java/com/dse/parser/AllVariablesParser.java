package com.dse.parser;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AllVariablesParser extends ASTVisitor {
    private ArrayList<IASTIdExpression> idExpressions = new ArrayList<>();

    public ArrayList<IASTIdExpression> getIdExpressions() {
        return idExpressions;
    }

    public AllVariablesParser() {
        shouldVisitExpressions = true;
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTExpression expression) {
//        if (expression instanceof IASTIdExpression){
//            IASTIdExpression idEx = (IASTIdExpression) expression;
//            String property = idEx.getPropertyInParent().getName();
//            if(! property.contains("FUNCTION_NAME"))
//                idExpressions.add(idEx);
//        }
        if (expression instanceof IASTIdExpression){
            IASTIdExpression idEx = (IASTIdExpression) expression;
            idExpressions.add(idEx);
        }
        return PROCESS_CONTINUE;
    }
}
