package com.sapog87.visual_novel.core.json;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Root{
    private Drawflow drawflow;

    @Override
    public String toString() {
        return "Root{" +
                "drawflow=" + drawflow +
                '}';
    }
}
