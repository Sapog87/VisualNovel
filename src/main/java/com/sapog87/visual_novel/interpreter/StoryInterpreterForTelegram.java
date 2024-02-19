package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.parser.SemanticType;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.VariableInfo;
import com.sapog87.visual_novel.core.story.nodes.*;
import com.sapog87.visual_novel.spring.repository.StoryUserRepository;
import com.sapog87.visual_novel.spring.repository.StoryVariableRepository;
import com.sapog87.visual_novel.spring.repository.entity.User;
import com.sapog87.visual_novel.spring.repository.entity.Variable;
import com.sapog87.visual_novel.spring.service.StoryService;
import com.sapog87.visual_novel.telegram.Button;
import com.sapog87.visual_novel.telegram.StoryNodeWrapperForTelegramBot;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
public class StoryInterpreterForTelegram {
    private final StoryService service;
    private final StoryVariableRepository variableRepository;
    private final StoryUserRepository userRepository;
    private Story story;

    public StoryInterpreterForTelegram(StoryService service, StoryVariableRepository variableRepository, StoryUserRepository userRepository) {
        this.service = service;
        this.variableRepository = variableRepository;
        this.userRepository = userRepository;
    }

    public void load(String path) {
        Root root = service.loadStoryFromFile(path);
        story = new Story(root);
    }

    public StoryNodeWrapperForTelegramBot next(Long telegramUserId, String storyNodeId) {
        User user = userRepository
                .findUserByTelegramUserId(telegramUserId)
                .orElseGet(() -> initUser(telegramUserId));

        String nodeId = storyNodeId != null ? storyNodeId : story.getStartNodeId();
        StoryNode node = story.getNodeById(nodeId);
        while (!(node instanceof StartStoryNode || node instanceof IntermediateStoryNode || node instanceof EndStoryNode)) {
            if (node instanceof DelayStoryNode delayStoryNode) {
                long delay = Long.parseLong(delayStoryNode.getData().get("time"));
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    //TODO
                    throw new RuntimeException(e);
                }
                nodeId = delayStoryNode.getNext().get(0).get(0);
            } else if (node instanceof IfElseStoryNode ifElseStoryNode) {
                var variables = user.getVariables();
                boolean result = ifElseStoryNode.compute(toVariableInfo(variables));
                if (result) {
                    nodeId = ifElseStoryNode.getNext().get(0).get(0);
                } else {
                    nodeId = ifElseStoryNode.getNext().get(1).get(0);
                }
            } else if (node instanceof ActionStoryNode actionStoryNode) {
                var variables = user.getVariables();
                VariableInfo result = actionStoryNode.compute(toVariableInfo(variables));
                Variable variable = user.getVariables().get(result.getName());
                variable.setValue(result.getValue());
                variableRepository.saveAndFlush(variable);
                nodeId = actionStoryNode.getNext().get(0).get(0);
            } else {
                //TODO
                throw new IllegalArgumentException(nodeId);
            }
            node = story.getNodeById(nodeId);
        }

        user.setStoryNodeId(nodeId);
        userRepository.save(user);

        List<Button> buttons;
        if (node instanceof EndStoryNode) {
            buttons = new ArrayList<>();
        } else {
            buttons = node.getNext().get(0).stream().map(id -> {
                var storyButton = ((ButtonStoryNode) story.getNodeById(id));
                return new Button(
                        storyButton.getData().get("text"),
                        storyButton.getNext().get(0).get(0)
                );
            }).toList();
        }

        return new StoryNodeWrapperForTelegramBot(
                node.getData().get("text"),
                node.getData().get("picture"),
                buttons
        );
    }

    private User initUser(Long telegramUserId) {
        User user;
        user = new User();
        user.setTelegramUserId(telegramUserId);
        user.setStoryNodeId(story.getStartNodeId());
        List<Variable> variables = new ArrayList<>();
        for (var entry : story.getVariables().entrySet()) {
            VariableInfo info = entry.getValue();
            Variable variable = new Variable();
            variable.setName(info.getName());
            variable.setValue(info.getValue());
            variable.setUser(user);
            variables.add(variable);
        }
        userRepository.saveAndFlush(user);
        variableRepository.saveAllAndFlush(variables);
        return user;
    }

    private Map<String, VariableInfo> toVariableInfo(Map<String, Variable> variables) {
        return variables.entrySet()
                .stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> new VariableInfo(
                                        entry.getKey(),
                                        entry.getValue().getValue(),
                                        SemanticType.UNDEF
                                )
                        )
                );
    }
}
