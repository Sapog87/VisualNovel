package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.nodes.variable.VariableInfo;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class StoryNode implements Validation {
    private String id;
    private List<List<String>> next;
    private List<List<String>> prev;
    private Map<String, String> data;
    private Map<String, StoryNode> story;
    private Map<String, VariableInfo> variables;

    static StoryNode create(StoryNodeBuilderImpl builder){
        StoryNode storyNode = builder.type.get();
        storyNode.id = builder.id;
        storyNode.next = builder.next;
        storyNode.prev = builder.prev;
        storyNode.data = builder.data;
        storyNode.story = builder.story;
        storyNode.variables = builder.variables;

        return storyNode;
    }

    public static StoryNodeBuilder newBuilder() {
        return new StoryNodeBuilderImpl();
    }

    public interface StoryNodeBuilder {
        StoryNodeBuilder id(String nodeId);
        StoryNodeBuilder type(Supplier<StoryNode> type);
        StoryNodeBuilder next(List<List<String>> next);
        StoryNodeBuilder prev(List<List<String>> prev);
        StoryNodeBuilder data(Map<String, String> data);
        StoryNodeBuilder story(Map<String, StoryNode> story);
        StoryNodeBuilder variables(Map<String, VariableInfo> variables);
        StoryNode build();
    }

}
