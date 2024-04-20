package com.sapog87.visual_novel.core.story.nodes.variable;

import com.sapog87.visual_novel.core.parser.ParseException;
import com.sapog87.visual_novel.core.parser.Parser;
import com.sapog87.visual_novel.core.parser.SemanticType;
import com.sapog87.visual_novel.core.parser.TokenMgrError;
import com.sapog87.visual_novel.core.story.Utility;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class VariableStoryNode extends StoryNode {
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
        if (!getData().containsKey("statetype")) {
            throw new IllegalArgumentException("node must have {statetype} field");
        }
        if (getData().get("statetype").isBlank()) {
            throw new IllegalArgumentException("{statetype} field in node must not be empty");
        }

        try {
            validateType();
        } catch (ParseException | TokenMgrError e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void validateType() throws ParseException {
        SemanticType semanticType = Utility.toSemanticType(getData().get("type"));
        Parser parser = getParser();
        switch (semanticType) {
            case STRING -> parser.parseString();
            case REAL -> {
                try {
                    parser.parseDouble();
                } catch (ParseException e) {
                    parser = getParser();
                    parser.parseInt();
                }
            }
            case INT -> parser.parseInt();
            case BOOL -> parser.parseBool();
        }
    }

    private Parser getParser() {
        String initialString = getData().get("value");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        return new Parser(targetStream);
    }
}
