package com.sapog87.visual_novel.core.parser;

public class Real extends Num {
    Real(Double v) {super(v);}

    @Override
    public void visit(Visitor v) {
        v.visitReal(this);
    }
}
