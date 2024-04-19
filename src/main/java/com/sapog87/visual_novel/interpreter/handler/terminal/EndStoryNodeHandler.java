package com.sapog87.visual_novel.interpreter.handler.terminal;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.EndStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.front.NodeWrapper;
import com.sapog87.visual_novel.interpreter.Utility;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EndStoryNodeHandler implements StoryNodeHandler<NodeWrapper> {
    @Override
    public NodeWrapper handle(StoryNode node, User user, Story story, Data data) {
        if (data != null)
            //TODO поменять на property
            return new NodeWrapper("","Я вас не понимаю", "", List.of());
        return Utility.getStoryNodeWrapper(node, story);
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return EndStoryNode.class;
    }
}
