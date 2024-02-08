package com.sapog87.visual_novel.core.parser;

public class Int extends Num {
    Int(Integer v) {super(v);}

    @Override
    public void visit(Visitor v) {
        v.visitInt(this);
    }
}
