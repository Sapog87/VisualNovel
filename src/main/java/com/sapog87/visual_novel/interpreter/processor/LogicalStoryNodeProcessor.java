package com.sapog87.visual_novel.interpreter.processor;

import com.sapog87.visual_novel.core.story.nodes.logical.LogicalStoryNode;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Transactional
public class LogicalStoryNodeProcessor extends Processor<String, LogicalStoryNode> {
    public LogicalStoryNodeProcessor(Map<String, StoryNodeHandler<String>> handlerMap) {
        super(handlerMap, LogicalStoryNode.class);
    }
}