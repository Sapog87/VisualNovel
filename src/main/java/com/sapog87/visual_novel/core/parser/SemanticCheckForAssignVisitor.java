package com.sapog87.visual_novel.core.parser;

import java.util.Map;

public class SemanticCheckForAssignVisitor extends SemanticCheckVisitor {
    SemanticCheckForAssignVisitor(Map<String, SemanticType> symTable) {
        super(symTable);
    }

    @Override
    void visitAssignExpr(AssignExpr assignExpr) {
        var typeOfId = calcType(assignExpr.id);
        var typeOfExpr = calcType(assignExpr.expr);
        var typeOfAssign = assignExpr.op;

        switch (typeOfAssign) {
            case "=":
                if ((typeOfId == SemanticType.BOOL && typeOfExpr != SemanticType.BOOL) ||
                        (typeOfId == SemanticType.STRING && typeOfExpr != SemanticType.STRING) ||
                        (typeOfId == SemanticType.INT && typeOfExpr != SemanticType.INT) ||
                        (typeOfId == SemanticType.REAL && typeOfExpr != SemanticType.INT && typeOfExpr != SemanticType.REAL)) {
                    throw new IllegalArgumentException("Operator {" + typeOfAssign + "} cannot be applied to Id: {" + typeOfId + "}, Expr {" + typeOfExpr + "}");
                }
                break;
            case "&=":
            case "|=":
                if ((typeOfId == SemanticType.REAL || typeOfExpr == SemanticType.REAL) ||
                        (typeOfId == SemanticType.STRING || typeOfExpr == SemanticType.STRING) ||
                        (typeOfId != typeOfExpr)) {
                    throw new IllegalArgumentException("Operator {" + typeOfAssign + "} cannot be applied to Id: {" + typeOfId + "}, Expr {" + typeOfExpr + "}");
                }
                break;
            case "+=":
            case "-=":
            case "*=":
            case "/=":
            case "%=":
                if ((typeOfId == SemanticType.BOOL || typeOfExpr == SemanticType.BOOL) ||
                        (typeOfId == SemanticType.STRING || typeOfExpr == SemanticType.STRING)) {
                    throw new IllegalArgumentException("Operator {" + typeOfAssign + "} cannot be applied to Id: {" + typeOfId + "}, Expr {" + typeOfExpr + "}");
                }
                break;
        }
    }
}
