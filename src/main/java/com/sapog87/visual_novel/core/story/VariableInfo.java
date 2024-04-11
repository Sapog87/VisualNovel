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
    private Boolean permanent;

    public VariableInfo(String name, Object value, SemanticType type, Boolean permanent) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.permanent = permanent;
    }
}
