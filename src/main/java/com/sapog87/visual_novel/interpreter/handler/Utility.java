package com.sapog87.visual_novel.interpreter.handler;

import com.sapog87.visual_novel.app.entity.Variable;
import com.sapog87.visual_novel.core.story.VariableInfo;

import java.util.Map;
import java.util.stream.Collectors;

public final class Utility {
    private Utility() {
    }

    public static Map<String, VariableInfo> toVariableInfo(Map<String, Variable> variables) {
        return variables.entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new VariableInfo(
                                        entry.getKey(),
                                        entry.getValue().getValue(),
                                        entry.getValue().getType(),
                                        entry.getValue().getPermanent()
                                )
                        )
                );
    }
}
