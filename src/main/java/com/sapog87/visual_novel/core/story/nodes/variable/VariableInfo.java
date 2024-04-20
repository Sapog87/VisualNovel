package com.sapog87.visual_novel.core.story.nodes.variable;

import com.sapog87.visual_novel.core.parser.SemanticType;
import lombok.Getter;

@Getter
public final class VariableInfo {
    private final String name;
    private final Object value;
    private final SemanticType type;
    private final Boolean permanent;

    public VariableInfo(String name, Object value, SemanticType type, Boolean permanent) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.permanent = permanent;
    }
}
