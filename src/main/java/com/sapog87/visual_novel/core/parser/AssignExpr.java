package com.sapog87.visual_novel.core.parser;

import lombok.Getter;

@Getter
public class AssignExpr extends Expr {
    Expr id;
    Expr expr;
    String op;
    AssignExpr(Expr id, Expr expr, String op) {this.id = id; this.expr = expr; this.op = op;}
    public String toString() {return id + " " + op + " " + expr;}

    @Override
    public void visit(Visitor v) {
        v.visitAssignExpr(this);
    }
}
