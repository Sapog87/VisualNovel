package com.sapog87.visual_novel.core.story.nodes.terminal;

import org.springframework.stereotype.Component;

@Component
public final class IntermediateStoryNode extends TerminalStoryNode {
    @Override
    public void validate() {
        if (getNext().isEmpty() || getPrev().isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and outputs");
        }
        if (getNext().get(0).isEmpty() || getPrev().get(0).isEmpty()) {
            throw new IllegalArgumentException("node must have inputs and 1 output");
        }
        super.validate();
    }
}
