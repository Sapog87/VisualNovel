package com.sapog87.visual_novel.core.story.nodes;

public class StoryNodeFactory extends AbstractStoryNodeFactory {
    @Override
    public StoryNode createStoryNode(StoryNodeType type) {
        return type.get();
    }
}
