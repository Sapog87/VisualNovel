package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.app.repository.StoryUserRepository;
import com.sapog87.visual_novel.app.repository.StoryVariableRepository;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.entity.Variable;
import com.sapog87.visual_novel.app.service.StoryService;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.VariableInfo;
import com.sapog87.visual_novel.core.story.nodes.*;
import com.sapog87.visual_novel.front.telegram.Button;
import com.sapog87.visual_novel.front.telegram.MessageData;
import com.sapog87.visual_novel.front.telegram.StoryNodeWrapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class StoryInterpreter {
    private final StoryService service;
    private final StoryVariableRepository variableRepository;
    private final StoryUserRepository userRepository;
    private final StoryProcessor storyProcessor;
    List<Class<? extends StoryNode>> terminalNodes;
    private Story story;

    public StoryInterpreter(StoryService service, StoryVariableRepository variableRepository, StoryUserRepository userRepository, StoryProcessor storyProcessor, ApplicationContext applicationContext) {
        this.service = service;
        this.variableRepository = variableRepository;
        this.userRepository = userRepository;
        this.storyProcessor = storyProcessor;
        terminalNodes = new ArrayList<>();
        applicationContext.getBeansOfType(TerminalNode.class).values().forEach(x -> terminalNodes.add(x.getClass()));
    }

    public void load(String path) {
        Root root = service.loadStoryFromFile(path);
        story = new Story(root);
    }

    public StoryNodeWrapper current(Long telegramUserId) {
        var node = story.getNodeById(userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new).getStoryNodeId());
        return getStoryNodeWrapper(node);
    }

    protected StoryNodeWrapper getStoryNodeWrapper(StoryNode node) {
        List<Button> buttons = getButtons(node);

        return new StoryNodeWrapper(
                node.getData().get("text"),
                node.getData().get("picture"),
                buttons
        );
    }

    private List<Button> getButtons(StoryNode node) {
        if (node instanceof EndStoryNode) {
            return new ArrayList<>();
        } else {
            return node.getNext().get(0).stream().map(id -> {
                var storyButton = ((ButtonStoryNode) story.getNodeById(id));
                return new Button(storyButton.getData().get("text"), storyButton.getNext().get(0).get(0));
            }).toList();
        }
    }

    public StoryNodeWrapper next(Long telegramUserId, String storyNodeId, String userMessage) {
        if (userMessage == null)
            return proceed(telegramUserId, storyNodeId, false);
        return proceedAI(telegramUserId, storyNodeId);
    }

    private StoryNodeWrapper proceedAI(Long telegramUserId, String storyNodeId) {
        User user = userRepository.findUserByExternalUserId(telegramUserId).orElseGet(() -> initUser(telegramUserId));

        String nodeId = storyNodeId != null ? storyNodeId : story.getStartNodeId();
        StoryNode node = story.getNodeById(nodeId);
        if (node.getNodeType().equals(NodeType.API)) {
            //TODO написать запрос к API
        } else {
            //TODO поменять текст на property
            return new StoryNodeWrapper("Я вас не понимаю", "", List.of());
        }

        user.setStoryNodeId(nodeId);
        userRepository.save(user);

        return getStoryNodeWrapper(node);
    }

    public StoryNodeWrapper restart(Long telegramUserId) {
        return proceed(telegramUserId, null, true);
    }

    private StoryNodeWrapper proceed(Long telegramUserId, String storyNodeId, boolean restart) {
        User user = userRepository.findUserByExternalUserId(telegramUserId).orElseGet(() -> initUser(telegramUserId));

        if (restart) {
            restartStory(user);
        }

        String nodeId = storyNodeId != null ? storyNodeId : user.getStoryNodeId();
        StoryNode node = story.getNodeById(nodeId);
        while (!containsInstance(terminalNodes, node)) {
            nodeId = storyProcessor.processNode(node, user, story);
            node = story.getNodeById(nodeId);
        }

        user.setStoryNodeId(nodeId);
        userRepository.save(user);

        return getStoryNodeWrapper(node);
    }

    protected User initUser(Long telegramUserId) {
        User user;
        user = new User();
        user.setExternalUserId(telegramUserId);
        user.setStoryNodeId(story.getStartNodeId());
        List<Variable> variables = new ArrayList<>();
        for (var entry : story.getVariables().entrySet()) {
            VariableInfo info = entry.getValue();
            Variable variable = new Variable();
            variable.setName(info.getName());
            variable.setType(info.getType());
            variable.setValue(info.getValue());
            variable.setUser(user);
            variables.add(variable);
            variable.setPermanent(info.getPermanent());
        }
        userRepository.saveAndFlush(user);
        variableRepository.saveAllAndFlush(variables);
        return user;
    }

    protected void restartStory(User user) {
        List<Variable> v = variableRepository.findAllByUser(user);
        v.forEach(x -> {if (!x.getPermanent()) x.setValue(story.getVariables().get(x.getName()).getValue());});
        variableRepository.saveAllAndFlush(v);

        user.setStoryNodeId(story.getStartNodeId());
        userRepository.saveAndFlush(user);
    }

    protected boolean containsInstance(List<Class<? extends StoryNode>> classes, StoryNode node) {
        for (Class<? extends StoryNode> clazz : classes) {
            if (clazz.isInstance(node)) {
                return true;
            }
        }
        return false;
    }

    public void setLastMessageData(Long telegramUserId, MessageData data) {
        User user = userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new);
        user.setMessageId(data.getMessageId());
        user.setChatId(data.getChatId());
        userRepository.saveAndFlush(user);
    }

    public MessageData getLastMessageData(Long telegramUserId) {
        User user = userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new);
        MessageData data = new MessageData();
        data.setMessageId(user.getMessageId());
        data.setChatId(user.getChatId());
        return data;
    }
}
