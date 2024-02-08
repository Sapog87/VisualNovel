package com.sapog87.visual_novel.core.story;

import com.sapog87.visual_novel.core.parser.SemanticType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariableInfo {
    private String name;
    private Object value;
    private SemanticType type;

    public VariableInfo(String name, Object value, SemanticType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
}
