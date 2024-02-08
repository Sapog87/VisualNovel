package com.sapog87.visual_novel.core.parser;

public class Id extends Val {
    Id(String v) {super(v);}

    @Override
    public void visit(Visitor v) {
        v.visitId(this);
    }
}
