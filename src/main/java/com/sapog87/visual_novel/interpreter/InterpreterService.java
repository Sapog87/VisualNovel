package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.app.entity.Message;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.entity.Variable;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.app.repository.MessageRepository;
import com.sapog87.visual_novel.app.repository.UserRepository;
import com.sapog87.visual_novel.app.repository.VariableRepository;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.front.MessageData;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InterpreterService {

    private static final Logger log = LoggerFactory.getLogger(InterpreterService.class);
    private final VariableRepository variableRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public InterpreterService(VariableRepository variableRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.variableRepository = variableRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional(dontRollbackOn = UserNotFoundException.class)
    void setLastMessageData(Long telegramUserId, MessageData data) {
        log.info("Setting last message data for telegram user {}", telegramUserId);
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
        messageRepository.save(message);
    }

    //TODO не хранить текст в бд
    @Transactional(dontRollbackOn = UserNotFoundException.class)
    MessageData getLastMessageData(Long telegramUserId) {
        log.info("Getting last message data for telegram user {}", telegramUserId);
        User user = userRepository.findUserByExternalUserIdWithMessage(telegramUserId).orElseThrow(UserNotFoundException::new);
        Message message = user.getLastMessage();
        return MessageData.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .text(message.getLastMessageText())
                .nodeId(message.getNodeId()).build();
    }

    @Transactional
    void saveCurrentStoryState(User user, String nodeId) {
        log.info("Save current story state for user {}", user.getExternalUserId());
        user.setStoryNodeId(nodeId);
        user.setVersion(user.getVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    void restartStory(User user, Story story) {
        log.info("Restarting story for user {}", user.getExternalUserId());
        List<Variable> v = variableRepository.findAllByUser(user);
        v.forEach(x -> {
            if (!x.getPermanent())
                x.setValue(story.getVariables().get(x.getName()).getValue());
        });
        variableRepository.saveAll(v);

        user.setStoryNodeId(story.getStartNodeId());
        userRepository.save(user);
    }

    @Transactional
    User findUserOrInit(Long telegramUserId, Story story) {
        log.info("Finding user by telegram user id {}", telegramUserId);
        return userRepository
                .findUserByExternalUserId(telegramUserId)
                .orElseGet(() -> this.initUser(telegramUserId, story));
    }

    @Transactional
    User initUser(Long telegramUserId, Story story) {
        log.info("Initializing user {}", telegramUserId);
        User user = this.constructUser(telegramUserId, story);
        List<Variable> variables = story
                .getVariables().values().stream()
                .map(value -> new Variable(value, user))
                .collect(Collectors.toList());
        userRepository.save(user);
        variableRepository.saveAll(variables);
        return user;
    }

    private User constructUser(Long telegramUserId, Story story) {
        User user = new User();
        user.setExternalUserId(telegramUserId);
        user.setStoryNodeId(story.getStartNodeId());
        return user;
    }

    @Transactional(dontRollbackOn = UserNotFoundException.class)
    Integer version(Long telegramUserId) {
        log.info("Getting version of user {}", telegramUserId);
        return userRepository.findUserByExternalUserId(telegramUserId).orElseThrow(UserNotFoundException::new).getVersion();
    }
}