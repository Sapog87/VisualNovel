package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.parser.*;
import com.sapog87.visual_novel.core.story.VariableInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class IfElseStoryNode extends StoryNode {
    private Expr expr;
    public IfElseStoryNode(StoryNode node, Map<String, VariableInfo> variables) {
        super(node, variables);
    }

    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and outputs");
        }
        if (!getData().containsKey("condition")) {
            throw new IllegalArgumentException("node must have {condition} field");
        }
        if (getData().get("condition").isBlank()) {
            throw new IllegalArgumentException("{condition} field in node must not be empty");
        }

        Parser parser = getParser();

        Map<String, SemanticType> semanticTypeMap = getSemanticTypeMap();

        try {
            expr = parser.semanticCheckForCondition(semanticTypeMap);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Parser getParser() {
        String initialString = getData().get("condition");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        return new Parser(targetStream);
    }

    private Map<String, SemanticType> getSemanticTypeMap() {
        Map<String, SemanticType> semanticTypeMap = new HashMap<>();
        for (var x : getVariables().entrySet())
            semanticTypeMap.put(x.getKey(), x.getValue().getType());
        return semanticTypeMap;
    }
}
