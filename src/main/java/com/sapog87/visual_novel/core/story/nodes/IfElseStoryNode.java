package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.parser.*;
import com.sapog87.visual_novel.core.story.VariableInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IfElseStoryNode extends LogicalStoryNode {
    private Expr expr;

    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty())
            throw new IllegalArgumentException("node must have inputs and outputs");
        for (List<String> t : getNext())
            if (t.size() != 1)
                throw new IllegalArgumentException("each output must be connected to one node");
        if (!getData().containsKey("condition"))
            throw new IllegalArgumentException("node must have {condition} field");
        if (getData().get("condition").isBlank())
            throw new IllegalArgumentException("{condition} field in node must not be empty");
        super.validate();

        Parser parser = getParser();
        Map<String, SemanticType> semanticTypeMap = getSemanticTypeMap();
        try {
            expr = parser.semanticCheckForCondition(semanticTypeMap);
        } catch (ParseException | TokenMgrError e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Parser getParser() {
        String initialString = getData().get("condition");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        return new Parser(targetStream);
    }

    public Boolean compute(Map<String, VariableInfo> variables) {
        CalculationVisitor visitor = new CalculationVisitor(variables);
        expr.visit(visitor);
        return (Boolean) visitor.getResult();
    }
}
