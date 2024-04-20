package com.sapog87.visual_novel.interpreter.handler.logical;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.entity.Variable;
import com.sapog87.visual_novel.app.repository.VariableRepository;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.variable.VariableInfo;
import com.sapog87.visual_novel.core.story.nodes.logical.ActionStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import com.sapog87.visual_novel.interpreter.handler.Utility;
import org.springframework.stereotype.Component;

@Component
public class ActionStoryNodeHandler implements StoryNodeHandler<String> {

    private final VariableRepository variableRepository;

    public ActionStoryNodeHandler(VariableRepository variableRepository) {this.variableRepository = variableRepository;}

    @Override
    public String handle(StoryNode node, User user, Story story, Data data) {
        var variables = user.getVariables();
        VariableInfo result = ((ActionStoryNode) node).compute(Utility.toVariableInfo(variables));
        Variable variable = user.getVariables().get(result.getName());
        variable.setValue(result.getValue());
        variableRepository.saveAndFlush(variable);
        return node.getNext().get(0).get(0);
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return ActionStoryNode.class;
    }
}
