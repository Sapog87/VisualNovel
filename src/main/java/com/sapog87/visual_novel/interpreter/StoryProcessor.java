package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.interpreter.handler.DefaultStoryNodeHandler;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import com.sapog87.visual_novel.app.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StoryProcessor {
    private final Map<Class<? extends StoryNode>, StoryNodeHandler> handlers;

    public StoryProcessor(Map<String, StoryNodeHandler> handlerMap) {
        this.handlers = new HashMap<>();
        handlerMap.forEach((key, value) -> handlers.put(value.getHandledClass(), value));
    }

    public String processNode(StoryNode node, User user, Story story) {
        StoryNodeHandler handler = handlers.getOrDefault(node.getClass(), new DefaultStoryNodeHandler());
        return handler.handle(node, user, story);
    }
}