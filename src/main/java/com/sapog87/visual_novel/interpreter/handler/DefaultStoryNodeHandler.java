package com.sapog87.visual_novel.interpreter.handler;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.interpreter.data.Data;
import org.springframework.stereotype.Component;

@Component
public class DefaultStoryNodeHandler<T> implements StoryNodeHandler<T> {
    @Override
    public T handle(StoryNode node, User user, Story story, Data data) {
        throw new IllegalArgumentException();
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return StoryNode.class;
    }
}
