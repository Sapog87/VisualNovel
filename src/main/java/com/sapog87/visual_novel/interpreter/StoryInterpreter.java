package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.app.service.StoryService;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.core.story.nodes.TerminalStoryNode;
import com.sapog87.visual_novel.front.MessageData;
import com.sapog87.visual_novel.front.NodeWrapper;
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
        log.info("Proceeding story for user {}", telegramUserId);
        return this.proceed(telegramUserId, storyNodeId, data, false);
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

        interpreterService.saveCurrentStoryState(user, nodeId);

        return terminalStoryNodeProcessor.processNode(node, user, story, data);
    }

    @Transactional
    public NodeWrapper restart(Long telegramUserId) {
        log.info("Restarting story for user {}", telegramUserId);
        return this.proceed(telegramUserId, null, null, true);
    }

    @Transactional(dontRollbackOn = UserNotFoundException.class)
    public Integer version(Long telegramUserId) {
        log.info("Getting version for user {}", telegramUserId);
        return interpreterService.version(telegramUserId);
    }
}
