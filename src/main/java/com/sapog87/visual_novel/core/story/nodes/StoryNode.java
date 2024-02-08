package com.sapog87.visual_novel.core.story.nodes;

import com.sapog87.visual_novel.core.story.VariableInfo;
import lombok.Getter;
import lombok.Setter;

import javax.xml.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StoryNode implements Validation {
    private NodeType nodeType;
    private List<List<String>> next;
    private List<List<String>> prev;
    private Map<String, String> data;
    private Map<String, StoryNode> story;
    private Map<String, VariableInfo> variables;

    public StoryNode() {
        this.nodeType = NodeType.UNDEF;
        this.next = new ArrayList<>();
        this.prev = new ArrayList<>();
        this.data = new HashMap<>();
        this.story = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public StoryNode(StoryNode node, Map<String, VariableInfo> variables) {
        this.nodeType = node.getNodeType();
        this.next = node.getNext();
        this.prev = node.getPrev();
        this.data = node.getData();
        this.story = node.getStory();
        this.variables = variables;
    }

    @Override
    public void validate() {
        throw new UnsupportedOperationException("function must be realized in sub class");
    }
}
