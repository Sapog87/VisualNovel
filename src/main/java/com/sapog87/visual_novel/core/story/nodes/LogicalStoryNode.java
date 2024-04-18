package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.parser.SemanticType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LogicalStoryNode extends StoryNode {
    @Override
    public void validate() {
        for (List<String> outputs : getNext().isEmpty() ? new ArrayList<List<String>>() : getNext())
            for (String id : outputs)
                if (getStory().get(id) instanceof ButtonStoryNode)
                    throw new IllegalArgumentException("output of node must not be button");
    }

    protected Map<String, SemanticType> getSemanticTypeMap() {
        Map<String, SemanticType> semanticTypeMap = new HashMap<>();
        for (var x : getVariables().entrySet())
            semanticTypeMap.put(x.getKey(), x.getValue().getType());
        return semanticTypeMap;
    }
}
