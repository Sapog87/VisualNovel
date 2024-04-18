package com.sapog87.visual_novel.interpreter.handler.logical;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.IfElseStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import org.springframework.stereotype.Component;

import static com.sapog87.visual_novel.interpreter.handler.Helper.toVariableInfo;

@Component
public class IfElseStoryNodeHandler implements StoryNodeHandler<String> {
    @Override
    public String handle(StoryNode node, User user, Story story, Data data) {
        var variables = user.getVariables();
        boolean result = ((IfElseStoryNode) node).compute(toVariableInfo(variables));
        return result ? node.getNext().get(0).get(0) : node.getNext().get(1).get(0);
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return IfElseStoryNode.class;
    }
}
