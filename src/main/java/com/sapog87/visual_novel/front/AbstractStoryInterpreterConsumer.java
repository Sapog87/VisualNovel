package com.sapog87.visual_novel.front;

import com.sapog87.visual_novel.interpreter.StoryInterpreter;
import lombok.Getter;

public abstract class AbstractStoryInterpreterConsumer {
    protected final StoryInterpreter interpreter;

    public AbstractStoryInterpreterConsumer(StoryInterpreter storyInterpreter) {
        this.interpreter = storyInterpreter;
    }
}
