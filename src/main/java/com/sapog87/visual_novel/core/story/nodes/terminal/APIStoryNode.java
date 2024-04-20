package com.sapog87.visual_novel.core.story.nodes.terminal;

import com.sapog87.visual_novel.core.story.nodes.button.ButtonStoryNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public final class APIStoryNode extends TerminalStoryNode {
    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty())
            throw new IllegalArgumentException("node must have inputs and outputs");
        if (getNext().get(0).size() != 1 || getPrev().get(0).isEmpty())
            throw new IllegalArgumentException("node must have 1 or more inputs and 1 output");
        if (!getData().containsKey("url"))
            throw new IllegalArgumentException("node must have {url} field");
        if (getData().get("url").isBlank())
            throw new IllegalArgumentException("{url} field in node must not be empty");
        for (String id : getNext().isEmpty() ? new ArrayList<String>() : getNext().get(0))
            if (!(getStory().get(id) instanceof ButtonStoryNode))
                throw new IllegalArgumentException("all outputs of node must be buttons");
    }
}
