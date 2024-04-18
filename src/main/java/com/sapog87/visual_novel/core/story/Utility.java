package com.sapog87.visual_novel.core.story;

import com.sapog87.visual_novel.core.parser.SemanticType;

public final class Utility {
    private Utility() {
    }

    public static SemanticType toSemanticType(String typeName) {
        return switch (typeName) {
            case "string" -> SemanticType.STRING;
            case "int" -> SemanticType.INT;
            case "double" -> SemanticType.REAL;
            case "bool" -> SemanticType.BOOL;
            default -> throw new IllegalStateException("Unexpected type: " + typeName);
        };
    }
}
