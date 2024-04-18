package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class StoryNodeBuilderImpl implements StoryNode.StoryNodeBuilder {
    String id;
    Supplier<StoryNode> type;
    List<List<String>> next;
    List<List<String>> prev;
    Map<String, String> data;
    Map<String, StoryNode> story;
    Map<String, VariableInfo> variables;

    @Override
    public StoryNode.StoryNodeBuilder id(String nodeId) {
        this.id = nodeId;
        return this;
    }

    @Override
    public StoryNode.StoryNodeBuilder type(Supplier<StoryNode> type) {
        this.type = type;
        return this;
    }

    @Override
    public StoryNode.StoryNodeBuilder next(List<List<String>> next) {
        this.next = next;
        return this;
    }

    @Override
    public StoryNode.StoryNodeBuilder prev(List<List<String>> prev) {
        this.prev = prev;
        return this;
    }

    @Override
    public StoryNode.StoryNodeBuilder data(Map<String, String> data) {
        this.data = data;
        return this;
    }

    @Override
    public StoryNode.StoryNodeBuilder story(Map<String, StoryNode> story) {
        this.story = story;
        return this;
    }

    @Override
    public StoryNode.StoryNodeBuilder variables(Map<String, VariableInfo> variables) {
        this.variables = variables;
        return this;
    }

    @Override
    public StoryNode build() {
        return StoryNode.create(this);
    }
}
