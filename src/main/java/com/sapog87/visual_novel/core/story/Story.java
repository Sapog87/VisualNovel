package com.sapog87.visual_novel.core.story;

import com.sapog87.visual_novel.core.json.Connection;
import com.sapog87.visual_novel.core.json.Node;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.parser.SemanticType;
import com.sapog87.visual_novel.core.story.nodes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Story {
    private final Logger logger = LoggerFactory.getLogger(Story.class);
    private final Map<String, StoryNode> story;
    private final Map<String, VariableInfo> variables;

    private void validateWholeStory() {
        boolean hasStart = false;
        boolean hasEnd = false;

        for (var node : story.entrySet()) {
            if (!node.getValue().getNodeType().equals(NodeType.START)) {
                if (node.getValue().getNodeType().equals(NodeType.END)) {
                    hasEnd = true;
                }
            } else if (hasStart) {
                throw new IllegalArgumentException("can't be 2 start nodes");
            } else {
                hasStart = true;
            }

            try {
                node.getValue().validate();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("node with id: {" + node.getKey() + "} is invalid", e);
            }
        }

        if (!hasStart) {
            throw new IllegalArgumentException("must be start node");
        }
        if (!hasEnd) {
            throw new IllegalArgumentException("must be at least 1 end node");
        }
    }

    public Story(Root root) {
        story = new HashMap<>();
        variables = new HashMap<>();
        List<VariableStoryNode> variableStoryNodes = new ArrayList<>();
        var treeNodes = root.getDrawflow().getHome().getData();
        for (var treeNode : treeNodes.entrySet()) {
            if (!story.containsKey(treeNode.getKey())) {
                Node node = treeNode.getValue();
                StoryNode storyNode = toStoryNode(node);
                if (storyNode instanceof VariableStoryNode variableStoryNode) {
                    variableStoryNodes.add(variableStoryNode);
                } else {
                    story.put(treeNode.getKey(), storyNode);
                }
            }
        }
        toVariables(variableStoryNodes);
        validateWholeStory();
        logger.info("Successful tree building");
    }

    private void toVariables(List<VariableStoryNode> variableStoryNodes) {
        for (VariableStoryNode node : variableStoryNodes) {
            node.validate();

            String name = node.getData().get("name");

            String t = node.getData().get("type");
            SemanticType semanticType = getSemanticType(t);

            String v = node.getData().get("value");
            Object value = switch (semanticType) {
                case STRING -> String.valueOf(v);
                case REAL -> Double.valueOf(v);
                case INT -> Integer.valueOf(v);
                case BOOL -> Boolean.valueOf(v);
            };

            variables.put(name, new VariableInfo(name, value, semanticType));
        }
    }

    public static SemanticType getSemanticType(String t) {
        return switch (t) {
            case "string" -> SemanticType.STRING;
            case "int" -> SemanticType.INT;
            case "double" -> SemanticType.REAL;
            case "bool" -> SemanticType.BOOL;
            default -> throw new IllegalStateException("Unexpected value: " + t);
        };
    }

    private StoryNode toStoryNode(Node node) {
        NodeType type = NodeType.valueOf(node.getMyClass().toUpperCase());
        StoryNode storyNode = new StoryNode();
        storyNode.setStory(story);
        storyNode.setNodeType(type);
        for (var output : node.getOutputs().entrySet()) {
            List<String> ids = new ArrayList<>();
            for (Connection connection : output.getValue().getConnections()) {
                String id = connection.getNode();
                ids.add(id);
            }
            storyNode.getNext().add(ids);
        }
        for (var input : node.getInputs().entrySet()) {
            List<String> ids = new ArrayList<>();
            for (Connection connection : input.getValue().getConnections()) {
                String id = connection.getNode();
                ids.add(id);
            }
            storyNode.getPrev().add(ids);
        }
        storyNode.setData(node.getData());
        return storyNodeByType(storyNode);
    }

    private StoryNode storyNodeByType(StoryNode node) {
        StoryNode storyNode = null;
        switch (node.getNodeType()) {
            case START -> storyNode = new StartStoryNode(node, variables);
            case END -> storyNode = new EndStoryNode(node, variables);
            case INTERMEDIATE -> storyNode = new IntermediateStoryNode(node, variables);
            case DELAY -> storyNode = new DelayStoryNode(node, variables);
            case ACTION -> storyNode = new ActionStoryNode(node, variables);
            case BUTTON -> storyNode = new ButtonStoryNode(node, variables);
            case IFELSE -> storyNode = new IfElseStoryNode(node, variables);
            case VARIABLE -> storyNode = new VariableStoryNode(node, variables);
            default -> throw new IllegalArgumentException("unsupported node type");
        }
        return storyNode;
    }
}
