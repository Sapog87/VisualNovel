package com.sapog87.visual_novel.core.json;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Root implements Serializable {
    private Drawflow drawflow;

    @Override
    public String toString() {
        return "Root{" +
                "drawflow=" + drawflow +
                '}';
    }
}
