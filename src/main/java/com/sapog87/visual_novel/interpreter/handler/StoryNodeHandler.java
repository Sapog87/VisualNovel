package com.sapog87.visual_novel.interpreter.handler;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.interpreter.data.Data;

public interface StoryNodeHandler<T> {
    T handle(StoryNode node, User user, Story story, Data data);
    Class<? extends StoryNode> getHandledClass();
}
