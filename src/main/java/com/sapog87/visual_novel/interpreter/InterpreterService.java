package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.app.entity.Message;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.entity.Variable;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.app.repository.MessageRepository;
import com.sapog87.visual_novel.app.repository.StoryUserRepository;
import com.sapog87.visual_novel.app.repository.StoryVariableRepository;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.front.telegram.MessageData;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterpreterService {

    private final StoryVariableRepository variableRepository;
    private final StoryUserRepository userRepository;
    private final MessageRepository messageRepository;

    public InterpreterService(StoryVariableRepository variableRepository, StoryUserRepository userRepository, MessageRepository messageRepository) {
        this.variableRepository = variableRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    void setLastMessageData(Long telegramUserId, MessageData data) {
        User user = userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new);
        Message message = Objects.requireNonNullElseGet(user.getLastMessage(), () -> {
            var m = new Message();
            m.setUser(user);
            return m;
        });
        message.setMessageId(data.getMessageId());
        message.setChatId(data.getChatId());
        message.setLastMessageText(data.getText());
        message.setNodeId(data.getNodeId());
        messageRepository.saveAndFlush(message);
    }

    //TODO не хранить текст в бд
    MessageData getLastMessageData(Long telegramUserId) {
        User user = userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new);
        Message message = user.getLastMessage();
        return MessageData.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .text(message.getLastMessageText())
                .nodeId(message.getNodeId()).build();
    }

    void saveCurrentStoryStateIfChanged(User user, String nodeId) {
        user.setStoryNodeId(nodeId);
        user.setVersion(user.getVersion() + 1);
        userRepository.save(user);
    }

    void restartStory(User user, Story story) {
        List<Variable> v = variableRepository.findAllByUser(user);
        v.forEach(x -> {
            if (!x.getPermanent())
                x.setValue(story.getVariables().get(x.getName()).getValue());
        });
        variableRepository.saveAllAndFlush(v);

        user.setStoryNodeId(story.getStartNodeId());
        userRepository.saveAndFlush(user);
    }

    User findUserOrInit(Long telegramUserId, Story story) {
        return userRepository
                .findUserByExternalUserId(telegramUserId)
                .orElseGet(() -> initUser(telegramUserId, story));
    }

    User initUser(Long telegramUserId, Story story) {
        User user = constructUser(telegramUserId, story);
        List<Variable> variables = story
                .getVariables().values().stream()
                .map(value -> new Variable(value, user))
                .collect(Collectors.toList());
        userRepository.saveAndFlush(user);
        variableRepository.saveAllAndFlush(variables);
        return user;
    }

    private User constructUser(Long telegramUserId, Story story) {
        User user = new User();
        user.setExternalUserId(telegramUserId);
        user.setStoryNodeId(story.getStartNodeId());
        return user;
    }

    public Integer version(Long telegramUserId) {
        return userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new).getVersion();
    }
}
