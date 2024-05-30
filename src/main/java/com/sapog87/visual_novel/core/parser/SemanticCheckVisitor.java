package com.sapog87.visual_novel.core.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SemanticCheckVisitor extends Visitor {
    Set<String> arithmeticOperations = new HashSet<String>(List.of("+", "-", "*", "/", "%"));
    Set<String> logicalOperations = new HashSet<String>(List.of("&&", "||"));
    Set<String> compareOperations = new HashSet<String>(List.of(">", "<", ">=", "<=", "==", "!="));

    Map<String, SemanticType> symTable;

    SemanticCheckVisitor(Map<String, SemanticType> symTable) {
        this.symTable = symTable;
    }

    SemanticType calcType(Expr expr) {
        if (expr instanceof Id) {
            Id id = (Id)expr;
            if (!symTable.containsKey(id.value)) {
                throw new IllegalArgumentException("identifier {" + id + "} not defined");
            }
            return symTable.get(id.value);
        }
        else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            var left = this.calcType(binaryExpr.left);
            var right = this.calcType(binaryExpr.right);

            if (arithmeticOperations.contains(binaryExpr.op)) {
                if ((left.equals(SemanticType.REAL) || left.equals(SemanticType.INT)) && (right.equals(SemanticType.REAL) || right.equals(SemanticType.INT))) {
                    if(left.equals(right)) {
                        return left;
                    }
                    else {
                        return SemanticType.REAL;
                    }
                }
                else {
                    throw new IllegalArgumentException("unsupported operation {" + binaryExpr.op + "} for types: {" + left + "} and {" + right + "}");
                }
            }
            else if (logicalOperations.contains(binaryExpr.op)) {
                if (!left.equals(SemanticType.BOOL) || !right.equals(SemanticType.BOOL)) {
                    throw new IllegalArgumentException("unsupported operation {" + binaryExpr.op + "} for types: {" + left + "} and {" + right + "}");
                }
                return SemanticType.BOOL;
            }
            else if(compareOperations.contains(binaryExpr.op)){
                if ((left.equals(SemanticType.REAL) || left.equals(SemanticType.INT)) && (right.equals(SemanticType.REAL) || right.equals(SemanticType.INT))) {
                    return SemanticType.BOOL;
                }
                else if(left.equals(SemanticType.STRING) && right.equals(SemanticType.STRING) || left.equals(SemanticType.BOOL) && right.equals(SemanticType.BOOL)){
                    if (binaryExpr.op.equals("==") || binaryExpr.op.equals("!=")) {
                        return SemanticType.BOOL;
                    }
                    else {
                        throw new IllegalArgumentException("unsupported operation {" + binaryExpr.op + "} for types: {" + left + "} and {" + right + "}");
                    }
                }
                else {
                    throw new IllegalArgumentException("unsupported operation {" + binaryExpr.op + "} for types: {" + left + "} and {" + right + "}");
                }
            }
            else {
                throw new IllegalArgumentException("unknown operation");
            }
        }
        else if (expr instanceof MyString){
            return SemanticType.STRING;
        }
        else if (expr instanceof Int){
            return SemanticType.INT;
        }
        else if (expr instanceof Real){
            return SemanticType.REAL;
        }
        else if (expr instanceof Bool){
            return SemanticType.BOOL;
        }
        else {
            throw new IllegalArgumentException("unknown type");
        }
    }
}
