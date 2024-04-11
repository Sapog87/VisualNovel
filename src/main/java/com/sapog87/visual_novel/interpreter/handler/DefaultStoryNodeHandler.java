package com.sapog87.visual_novel.interpreter.handler;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DefaultStoryNodeHandler implements StoryNodeHandler {
    @Override
    public String handle(StoryNode node, User user, Story story) {
        throw new IllegalArgumentException();
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return null;
    }
}
