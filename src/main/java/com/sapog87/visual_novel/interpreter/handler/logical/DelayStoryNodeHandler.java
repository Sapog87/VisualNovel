package com.sapog87.visual_novel.interpreter.handler.logical;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.logical.DelayStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import org.springframework.stereotype.Component;

@Component
public class DelayStoryNodeHandler implements StoryNodeHandler<String> {
    @Override
    public String handle(StoryNode node, User user, Story story, Data data) {
        long delay = Long.parseLong(node.getData().get("time"));
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return node.getNext().get(0).get(0);
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return DelayStoryNode.class;
    }
}