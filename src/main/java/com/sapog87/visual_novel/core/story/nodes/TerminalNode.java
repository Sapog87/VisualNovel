package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;

import java.util.Map;

public class TerminalNode extends StoryNode {
    public TerminalNode(StoryNode node, Map<String, VariableInfo> variables) {
        super(node, variables);
    }

    public TerminalNode() {
        super();
    }
}
