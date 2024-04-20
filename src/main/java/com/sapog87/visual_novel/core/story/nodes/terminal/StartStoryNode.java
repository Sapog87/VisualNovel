package com.sapog87.visual_novel.core.story.nodes.terminal;

import org.springframework.stereotype.Component;

@Component
public final class StartStoryNode extends TerminalStoryNode {
    @Override
    public void validate() {
        if (getNext().isEmpty() || !getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have only outputs");
        }
        if (getNext().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have outputs");
        }
        super.validate();
    }
}
