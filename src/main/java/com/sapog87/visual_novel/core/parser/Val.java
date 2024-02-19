package com.sapog87.visual_novel.core.parser;

import lombok.Getter;

@Getter
public abstract class Val extends Expr {
    Object value;

    Val(Object value) {this.value = value;}

    public String toString() {return String.valueOf(value);}
}
