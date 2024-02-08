package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;

import java.util.Map;

public class EndStoryNode extends StoryNode {
    public EndStoryNode(StoryNode node, Map<String, VariableInfo> variables) {super(node, variables);}

    @Override
    public void validate() {
        if (!getNext().isEmpty() || getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have only inputs");
        }
        if (getPrev().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have inputs");
        }
        if (!getData().containsKey("text")) {
            throw new IllegalArgumentException("node must have {text} field");
        }
        if (!getData().containsKey("picture")) {
            throw new IllegalArgumentException("node must have {picture} field");
        }
        if (getData().get("text").isBlank()) {
            throw new IllegalArgumentException("{text} field in node must not be empty");
        }
    }
}
