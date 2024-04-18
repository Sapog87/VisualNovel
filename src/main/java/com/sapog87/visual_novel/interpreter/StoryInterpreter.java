package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.service.StoryService;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.core.story.nodes.TerminalStoryNode;
import com.sapog87.visual_novel.front.telegram.MessageData;
import com.sapog87.visual_novel.front.telegram.NodeWrapper;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.processor.LogicalStoryNodeProcessor;
import com.sapog87.visual_novel.interpreter.processor.TerminalStoryNodeProcessor;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StoryInterpreter {
    private final StoryService storyService;
    private final InterpreterService interpreterService;
    private final LogicalStoryNodeProcessor logicalStoryNodeProcessor;
    private final TerminalStoryNodeProcessor terminalStoryNodeProcessor;
    private Story story;

    public StoryInterpreter(StoryService storyService, InterpreterService interpreterService, LogicalStoryNodeProcessor logicalStoryNodeProcessor, TerminalStoryNodeProcessor terminalStoryNodeProcessor) {
        this.storyService = storyService;
        this.interpreterService = interpreterService;
        this.logicalStoryNodeProcessor = logicalStoryNodeProcessor;
        this.terminalStoryNodeProcessor = terminalStoryNodeProcessor;
    }

    public void load(String path) {
        Root root = storyService.loadStoryFromFile(path);
        story = new Story(root);
    }

    public NodeWrapper getNode(String nodeId) {
        var node = story.getNodeById(nodeId);
        return Utility.getStoryNodeWrapper(node, story);
    }

    @Transactional
    public NodeWrapper next(Long telegramUserId, String storyNodeId, Data data) {
        return proceed(telegramUserId, storyNodeId, data, false);
    }

    private NodeWrapper proceed(Long telegramUserId, String storyNodeId, Data data, boolean restart) {
        User user = interpreterService.findUserOrInit(telegramUserId, story);

        if (restart)
            interpreterService.restartStory(user, story);

        String nodeId = storyNodeId != null ? storyNodeId : user.getStoryNodeId();
        StoryNode node = story.getNodeById(nodeId);

        while (!(node instanceof TerminalStoryNode)) {
            nodeId = logicalStoryNodeProcessor.processNode(node, user, story, data);
            node = story.getNodeById(nodeId);
        }

        interpreterService.saveCurrentStoryStateIfChanged(user, nodeId);

        return terminalStoryNodeProcessor.processNode(node, user, story, data);
    }

    @Transactional
    public NodeWrapper restart(Long telegramUserId) {
        return proceed(telegramUserId, null, null, true);
    }

    public void setLastMessageData(Long telegramUserId, MessageData data) {
        interpreterService.setLastMessageData(telegramUserId, data);
    }

    //TODO разобраться с транзакциями
    public MessageData getLastMessageData(Long telegramUserId) {
        return interpreterService.getLastMessageData(telegramUserId);
    }

    //TODO разобраться с транзакциями
    public Integer version(Long telegramUserId) {
        return interpreterService.version(telegramUserId);
    }
}
