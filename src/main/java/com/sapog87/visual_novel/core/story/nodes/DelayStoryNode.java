package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;

import java.util.Map;

public class DelayStoryNode extends StoryNode {
    public DelayStoryNode(StoryNode node, Map<String, VariableInfo> variables) {
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
        if (!getData().containsKey("time")) {
            throw new IllegalArgumentException("node must have {time} field");
        }
        if (getData().get("time").isBlank()) {
            throw new IllegalArgumentException("{time} field in node must not be empty");
        }
        try {
            double time = Double.parseDouble(getData().get("time"));
            if (time < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("{time} field in node must be positive number");
        }
    }
}
