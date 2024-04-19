package com.sapog87.visual_novel.interpreter.processor;

import com.sapog87.visual_novel.core.story.nodes.TerminalStoryNode;
import com.sapog87.visual_novel.front.NodeWrapper;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Transactional
public class TerminalStoryNodeProcessor extends Processor<NodeWrapper, TerminalStoryNode> {
    public TerminalStoryNodeProcessor(Map<String, StoryNodeHandler<NodeWrapper>> handlerMap) {
        super(handlerMap, TerminalStoryNode.class);
    }
}
