package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.parser.ParseException;
import com.sapog87.visual_novel.core.parser.Parser;
import com.sapog87.visual_novel.core.parser.SemanticType;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.VariableInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class VariableStoryNode extends StoryNode {
    public VariableStoryNode(StoryNode node, Map<String, VariableInfo> variables) {
        super(node, variables);
    }

    @Override
    public void validate() {
        if (!getNext().isEmpty() || !getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and outputs");
        }
        if (!getData().containsKey("type")) {
            throw new IllegalArgumentException("node must have {type} field");
        }
        if (getData().get("type").isBlank()) {
            throw new IllegalArgumentException("{type} field in node must not be empty");
        }
        if (!getData().containsKey("name")) {
            throw new IllegalArgumentException("node must have {name} field");
        }
        if (getData().get("name").isBlank()) {
            throw new IllegalArgumentException("{name} field in node must not be empty");
        }
        if (!getData().containsKey("value")) {
            throw new IllegalArgumentException("node must have {value} field");
        }
        if (getData().get("value").isBlank()) {
            throw new IllegalArgumentException("{value} field in node must not be empty");
        }

        try {
            validateType();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateType() throws ParseException {
        SemanticType semanticType = Story.getSemanticType(getData().get("type"));
        Parser parser = getParser();
        switch (semanticType) {
            case STRING -> parser.MyString();
            case REAL -> {
                try {
                    parser.Real();
                } catch (ParseException e) {
                    parser = getParser();
                    parser.Int();
                }
            }
            case INT -> parser.Int();
            case BOOL -> parser.Bool();
        }
    }

    private Parser getParser() {
        String initialString = getData().get("value");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        return new Parser(targetStream);
    }
}
