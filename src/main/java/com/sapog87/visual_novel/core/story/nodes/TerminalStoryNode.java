package com.sapog87.visual_novel.core.story.nodes;

import java.util.ArrayList;
import java.util.List;

public abstract class TerminalStoryNode extends StoryNode {
    @Override
    public void validate() {
        if (!getData().containsKey("text")) {
            throw new IllegalArgumentException("node must have {text} field");
        }
        if (getData().get("text").isBlank()) {
            throw new IllegalArgumentException("{text} field in node must not be empty");
        }
        if (!getData().containsKey("picture")) {
            throw new IllegalArgumentException("node must have {picture} field");
        }

        for (List<String> outputs : getNext().isEmpty() ? new ArrayList<List<String>>() : getNext())
            for (String id : outputs)
                if (!(getStory().get(id) instanceof ButtonStoryNode))
                    throw new IllegalArgumentException("all outputs of node must be buttons");
    }
}
