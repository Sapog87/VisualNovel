package com.sapog87.visual_novel.core.story;

import com.sapog87.visual_novel.core.json.Connection;
import com.sapog87.visual_novel.core.json.Node;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.parser.SemanticType;
import com.sapog87.visual_novel.core.story.nodes.*;
import com.sapog87.visual_novel.core.story.validation.ValidationErrorInfo;
import com.sapog87.visual_novel.core.story.validation.ValidationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Story {
    private final Map<String, StoryNode> story;
    private final Map<String, VariableInfo> variables;
    private final List<ValidationErrorInfo> errors = new ArrayList<>();
    @Getter
    private String startNodeId;

    public Story(Root root) {
        this(root, Map.of());
    }

    public Story(Root root, Map<String, Supplier<StoryNode>> customTypes) {
        story = new HashMap<>();
        variables = new HashMap<>();
        var types = this.concatBasicStoryNodeTypeMapWithCustom(customTypes);
        Map<String, VariableStoryNode> variableStoryNodes = new HashMap<>();
        for (var treeNode : root.getDrawflow().getHome().getData().entrySet()) {
            if (!story.containsKey(treeNode.getKey())) {
                Node node = treeNode.getValue();
                StoryNode storyNode = this.toStoryNode(node, variables, types);
                if (storyNode instanceof VariableStoryNode variableStoryNode) {
                    variableStoryNodes.put(treeNode.getKey(), variableStoryNode);
                } else {
                    if (storyNode instanceof StartStoryNode)
                        startNodeId = treeNode.getKey();
                    story.put(treeNode.getKey(), storyNode);
                }
            } else {
                throw new IllegalArgumentException("Node with such id already exists {" + treeNode.getKey() + "}");
            }
        }

        this.toVariables(variableStoryNodes);
        this.validate();

        if (!errors.isEmpty())
            throw new ValidationException(errors);

        log.info("Successful tree building");
    }

    private Map<String, Supplier<StoryNode>> concatBasicStoryNodeTypeMapWithCustom(Map<String, Supplier<StoryNode>> customTypes) {
        return Stream.concat(
                this.basicTypes().entrySet().stream(),
                customTypes.entrySet().stream().map(str ->
                        Map.entry(
                                str.getKey().toUpperCase(),
                                str.getValue()
                        )
                )
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private StoryNode toStoryNode(Node node, Map<String, VariableInfo> variables, Map<String, Supplier<StoryNode>> types) {
        var next = node.getOutputs().values()
                .stream().map(output -> output.getConnections()
                        .stream().map(Connection::getNode)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        var prev = node.getInputs().values()
                .stream().map(input -> input.getConnections()
                        .stream().map(Connection::getNode)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        Supplier<StoryNode> type = this.getStoryNodeSupplier(node, types);
        String id = String.valueOf(node.getId());
        return StoryNode.newBuilder().id(id).story(story).type(type).next(next).prev(prev).data(node.getData()).variables(variables).build();
    }

    private void toVariables(Map<String, VariableStoryNode> variableStoryNodes) {
        for (var node : variableStoryNodes.entrySet()) {
            if (!this.validateNode(node.getKey(), node.getValue()))
                continue;

            VariableInfo info = this.getVariableInfo(node);
            variables.put(info.getName(), info);
        }
    }

    private void validate() {
        boolean hasStart = false;
        boolean hasEnd = false;
        String startNodeId = null;

        for (var node : story.entrySet()) {
            if (!(node.getValue() instanceof StartStoryNode)) {
                if (node.getValue() instanceof EndStoryNode)
                    hasEnd = true;
            } else if (hasStart) {
                errors.add(new ValidationErrorInfo(startNodeId, "can't be 2 start nodes"));
                errors.add(new ValidationErrorInfo(node.getKey(), "can't be 2 start nodes"));
            } else {
                startNodeId = node.getKey();
                hasStart = true;
            }

            this.validateNode(node.getKey(), node.getValue());
        }

        if (!hasStart)
            errors.add(new ValidationErrorInfo(null, "must be start node"));
        if (!hasEnd)
            errors.add(new ValidationErrorInfo(null, "must be at least 1 end node"));
    }

    private Map<String, Supplier<StoryNode>> basicTypes() {
        return Arrays.stream(StoryNodeType.values()).collect(Collectors.toMap(StoryNodeType::name, storyNodeType -> storyNodeType));
    }

    private Supplier<StoryNode> getStoryNodeSupplier(Node node, Map<String, Supplier<StoryNode>> types) {
        try {
            String key = node.getMyClass().toUpperCase();
            if (types.containsKey(key))
                return types.get(key);
            else
                throw new IllegalArgumentException("Unsupported node type " + key);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(List.of(new ValidationErrorInfo(null, e.getMessage())));
        }
    }

    private boolean validateNode(String id, StoryNode value) {
        try {
            value.validate();
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationErrorInfo(id, e.getMessage()));
        }
        return errors.isEmpty();
    }

    private VariableInfo getVariableInfo(Map.Entry<String, VariableStoryNode> node) {
        String name = node.getValue().getData().get("name");
        SemanticType semanticType = Utility.toSemanticType(
                node.getValue().getData().get("type")
        );
        Boolean permanent = Boolean.valueOf(
                node.getValue().getData().get("statetype")
        );
        Object value = toJavaObject(
                semanticType,
                node.getValue().getData().get("value")
        );
        return new VariableInfo(name, value, semanticType, permanent);
    }

    private Object toJavaObject(SemanticType semanticType, String value) {
        return switch (semanticType) {
            case STRING -> String.valueOf(value).substring(1, value.length() - 1);
            case REAL -> Double.valueOf(value);
            case INT -> Integer.valueOf(value);
            case BOOL -> Boolean.valueOf(value);
            case UNDEF -> null;
        };
    }

    public Map<String, VariableInfo> getVariables() {
        return Map.copyOf(variables);
    }

    public StoryNode getNodeById(String id) {
        return story.get(id);
    }
}
