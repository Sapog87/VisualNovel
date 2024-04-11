package com.sapog87.visual_novel.interpreter.handler;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.app.entity.User;

public interface StoryNodeHandler {
    String handle(StoryNode node, User user, Story story);
    Class<? extends StoryNode> getHandledClass();
}
