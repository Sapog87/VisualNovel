package com.sapog87.visual_novel.core.parser;

public class BinaryExpr extends Expr {
    String op;
    Expr left, right;
    BinaryExpr(String o, Expr l, Expr r) {op = o; left = l; right = r;}
    public String toString() {return "(" + op + " " + left + " " + right + ")";}

    @Override
    public void visit(Visitor v) {
        v.visitBinaryExpr(this);
    }
}
