package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.parser.*;
import com.sapog87.visual_novel.core.story.VariableInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ActionStoryNode extends StoryNode {
    private Expr expr;

    public ActionStoryNode(StoryNode node, Map<String, VariableInfo> variables) {
        super(node, variables);
    }

    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and outputs");
        }
        if (getNext().get(0).size() != 1 || getPrev().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and 1 output");
        }
        if (!getData().containsKey("action")) {
            throw new IllegalArgumentException("node must have {action} field");
        }
        if (getData().get("action").isBlank()) {
            throw new IllegalArgumentException("{action} field in node must not be empty");
        }

        Parser parser = getParser();

        Map<String, SemanticType> semanticTypeMap = getSemanticTypeMap();

        try {
            expr = parser.semanticCheckForAssign(semanticTypeMap);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Parser getParser() {
        String initialString = getData().get("action");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        return new Parser(targetStream);
    }

    private Map<String, SemanticType> getSemanticTypeMap() {
        Map<String, SemanticType> semanticTypeMap = new HashMap<>();
        for (var x : getVariables().entrySet())
            semanticTypeMap.put(x.getKey(), x.getValue().getType());
        return semanticTypeMap;
    }

    public VariableInfo compute(Map<String, VariableInfo> variables) {
        CalculationVisitor visitor = new CalculationVisitor(variables);
        expr.visit(visitor);
        String name = (String) ((Id) ((AssignExpr) expr).getId()).getValue();
        return new VariableInfo(name, visitor.getResult(), SemanticType.UNDEF, false);
    }
}
