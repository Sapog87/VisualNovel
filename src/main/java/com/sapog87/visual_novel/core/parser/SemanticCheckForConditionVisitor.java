package com.sapog87.visual_novel.core.parser;

import java.util.Map;

public class SemanticCheckForConditionVisitor extends SemanticCheckVisitor {
    SemanticCheckForConditionVisitor(Map<String, SemanticType> symTable) {
        super(symTable);
    }

    @Override
    void visitAssignExpr(AssignExpr assignExpr) {
        throw new IllegalArgumentException("unknown operation");
    }

    @Override
    void visitBinaryExpr(BinaryExpr binaryExpr) {
        var type = this.calcType(binaryExpr);
        if (!type.equals(SemanticType.BOOL)) {
            throw new IllegalArgumentException("found {" + type + "} expected {Bool}");
        }
    }

    @Override
    void visitId(Id id) {
        var type = this.calcType(id);
        if (!type.equals(SemanticType.BOOL)) {
            throw new IllegalArgumentException("found {" + type + "} expected {Bool}");
        }
    }

    @Override
    void visitInt(Int num) {
        throw new IllegalArgumentException("found {Int} expected {Bool}");
    }

    @Override
    void visitReal(Real num) {
        throw new IllegalArgumentException("found {Real} expected {Bool}");
    }

    @Override
    void visitString(MyString str) {
        throw new IllegalArgumentException("found {String} expected {Bool}");
    }
}
