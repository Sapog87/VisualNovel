package com.sapog87.visual_novel.core.story.nodes.button;

import com.sapog87.visual_novel.core.story.nodes.StoryNode;

import java.util.ArrayList;

public final class ButtonStoryNode extends StoryNode {
    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and outputs");
        }
        if (getNext().get(0).size() != 1 || getPrev().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and 1 output");
        }
        if (!getData().containsKey("text")) {
            throw new IllegalArgumentException("node must have {text} field");
        }
        if (getData().get("text").isBlank()) {
            throw new IllegalArgumentException("{text} field in node must not be empty");
        }
        for (String id : getNext().isEmpty() ? new ArrayList<String>() : getNext().get(0)) {
            if (getStory().get(id) instanceof ButtonStoryNode)
                throw new IllegalArgumentException("output of button must not be button");
        }
    }
}
