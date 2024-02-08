package com.sapog87.visual_novel.core.parser;

public abstract class Visitor {
    void visitId(Id id) {}
    void visitInt(Int num) {}
    void visitReal(Real num) {}
    void visitBool(Bool bool) {}
    void visitString(MyString str) {}
    void visitBinaryExpr(BinaryExpr binaryExpr) {}
    void visitAssignExpr(AssignExpr assignExpr) {}
}
