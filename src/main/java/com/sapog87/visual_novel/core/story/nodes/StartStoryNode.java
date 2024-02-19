package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;

import java.util.Map;

public class StartStoryNode extends StoryNode {
    public StartStoryNode(StoryNode node, Map<String, VariableInfo> variables) {
        super(node, variables);
    }

    @Override
    public void validate() {
        if (getNext().isEmpty() || !getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have only outputs");
        }
        if (getNext().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have outputs");
        }
        if (!getData().containsKey("text")) {
            throw new IllegalArgumentException("node must have {text} field");
        }
        if (getData().get("text").isBlank()) {
            throw new IllegalArgumentException("{text} field in node must not be empty");
        }
        if (!getData().containsKey("picture")) {
            throw new IllegalArgumentException("node must have {picture} field");
        }
        for (String id : getNext().get(0)) {
            if (!getStory().get(id).getNodeType().equals(NodeType.BUTTON)) {
                throw new IllegalArgumentException("all outputs of node must be buttons");
            }
        }
    }
}
