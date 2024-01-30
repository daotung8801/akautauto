package com.dse.parser;

import org.eclipse.cdt.core.dom.ast.*;

import java.util.ArrayList;
import java.util.List;

public class ReturnStatementParser extends ASTVisitor {
    private ArrayList<IASTReturnStatement> returnStatements = new ArrayList<>();

    public ArrayList<IASTReturnStatement> getReturnStatements() {
        return returnStatements;
    }

    public ReturnStatementParser() {
        shouldVisitExpressions = true;
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof IASTReturnStatement)
            returnStatements.add((IASTReturnStatement) statement);
        return PROCESS_CONTINUE;
    }
}
