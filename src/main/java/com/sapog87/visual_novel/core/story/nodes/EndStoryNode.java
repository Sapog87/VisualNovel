package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NoArgsConstructor
public final class EndStoryNode extends TerminalStoryNode {
    @Override
    public void validate() {
        if (!getNext().isEmpty() || getPrev().isEmpty())
            throw new IllegalArgumentException("node must have only inputs");
        if (getPrev().get(0).isEmpty())
            throw new IllegalArgumentException("node must have inputs");
        super.validate();
    }
}
