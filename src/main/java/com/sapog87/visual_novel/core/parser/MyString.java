package com.sapog87.visual_novel.core.parser;

public class MyString extends Val {
    MyString(String v) {super(v);}

    @Override
    public void visit(Visitor v) {
        v.visitString(this);
    }
}
