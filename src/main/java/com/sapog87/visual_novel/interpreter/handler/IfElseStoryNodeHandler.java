package com.sapog87.visual_novel.interpreter.handler;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.IfElseStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.app.entity.User;
import org.springframework.stereotype.Component;

import static com.sapog87.visual_novel.interpreter.handler.Helper.toVariableInfo;

@Component
public class IfElseStoryNodeHandler implements StoryNodeHandler {
    @Override
    public String handle(StoryNode node, User user, Story story) {
        var variables = user.getVariables();
        boolean result = ((IfElseStoryNode) node).compute(toVariableInfo(variables));
        return result ? node.getNext().get(0).get(0) : node.getNext().get(1).get(0);
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return IfElseStoryNode.class;
    }
}
