package com.sapog87.visual_novel.core.parser;

public class Bool extends Val {
    Bool(Boolean v) {super(v);}

    @Override
    public void visit(Visitor v) {
        v.visitBool(this);
    }
}
