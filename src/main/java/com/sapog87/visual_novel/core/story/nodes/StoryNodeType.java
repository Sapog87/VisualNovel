package com.sapog87.visual_novel.core.story.nodes;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public enum StoryNodeType implements Supplier<StoryNode> {
    VARIABLE(VariableStoryNode::new),
    START(StartStoryNode::new),
    BUTTON(ButtonStoryNode::new),
    IFELSE(IfElseStoryNode::new),
    END(EndStoryNode::new),
    INTERMEDIATE(IntermediateStoryNode::new),
    ACTION(ActionStoryNode::new),
    DELAY(DelayStoryNode::new),
    API(APIStoryNode::new);

    private final Supplier<StoryNode> storyNodeSupplier;

    StoryNodeType(Supplier<StoryNode> storyNodeSupplier) {
        this.storyNodeSupplier = requireNonNull(storyNodeSupplier);
    }

    @Override
    public StoryNode get() {
        return storyNodeSupplier.get();
    }
}
