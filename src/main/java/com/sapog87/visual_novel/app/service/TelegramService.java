package com.sapog87.visual_novel.app.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.front.adapter.Button;
import com.sapog87.visual_novel.front.adapter.NodeWrapper;
import com.sapog87.visual_novel.front.adapter.UserMessage;
import com.sapog87.visual_novel.interpreter.StoryInterpreter;
import com.sapog87.visual_novel.interpreter.data.Data;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Slf4j
@Service
public class TelegramService {
    private final StoryInterpreter storyInterpreter;
    private final KeyboardFactory keyboardFactory;
    @Value("${domain}")
    private String domain;
    @Value("${story-location}")
    private String storyLocation;
    @Value("${pictures-location}")
    private String picturesLocation;
    @Value("${webapp-mode}")
    private Boolean useWebApp;

    public TelegramService(StoryInterpreter storyInterpreter) {
        this.storyInterpreter = storyInterpreter;
        this.keyboardFactory = new KeyboardFactory();
    }

    public void start(String fileName) {
        log.info("Web app mode: {}", useWebApp);
        if (useWebApp) {
            if (domain == null || domain.isBlank())
                throw new IllegalStateException("Domain is blank");
            log.info("Domain: {}", domain);
        }
        log.info("Story location: {}", storyLocation);
        log.info("Pictures-location: {}", picturesLocation);
        storyInterpreter.load(storyLocation + fileName);
    }

    @Transactional(dontRollbackOn = UserNotFoundException.class)
    public void handleMessage(TelegramBot bot, UserMessage userMessage) {
        if (userMessage == null)
            return;

        CallbackData callbackData = CallbackData.getCallbackData(userMessage);

        if (callbackData.version != null && !this.isVersionMatching(userMessage.getUserId(), callbackData))
            return;

        this.markPressedButton(bot, userMessage.getMessage(), callbackData);
        this.sendChatAction(bot, userMessage.getUserId());
        this.sendMessage(bot,
                userMessage.isStartFlag(),
                userMessage.isRestartFlag(),
                userMessage.getUserId(),
                callbackData,
                userMessage.getData()
        );
    }

    private boolean isVersionMatching(Long userId, CallbackData callbackData) {
        log.info("Checking version: {}", userId);
        return storyInterpreter.version(userId).equals(callbackData.version);
    }

    private void markPressedButton(TelegramBot bot, Message message, CallbackData callbackData) {
        if (callbackData.nodeId == null)
            return;

        InlineKeyboardMarkup keyboard = this.createInlineKeyboardMarkup(callbackData);
        EditMessageCaption editMessageCaption = this.buildEditMessageCaption(message, keyboard);
        var response = bot.execute(editMessageCaption);
        if (!response.isOk()) {
            EditMessageText editMessageText = this.buildEditMessageText(message, keyboard);
            bot.execute(editMessageText);
        }
    }

    private void sendChatAction(TelegramBot bot, Long userId) {
        ChatAction chatAction = ChatAction.typing;
        SendChatAction sendChatAction = new SendChatAction(userId, chatAction);
        bot.execute(sendChatAction);
    }

    private void sendMessage(TelegramBot bot, boolean startFlag, boolean restartFlag, Long userId, CallbackData callbackData, Data data) {
        log.info("Sending message to user: {}", userId);
        String nodeId = callbackData.nextNodeId;
        RequestData requestData = this.prepareRequestData(startFlag, restartFlag, userId, data, nodeId);
        this.sendRequest(bot, userId, requestData);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(CallbackData callbackData) {
        NodeWrapper node = storyInterpreter.getNode(callbackData.nodeId);
        return keyboardFactory.createMarkedInlineKeyboardMarkup(node, callbackData);
    }

    private EditMessageCaption buildEditMessageCaption(Message message, InlineKeyboardMarkup keyboard) {
        EditMessageCaption editMessageCaption = new EditMessageCaption(message.chat().id(), message.messageId());
        editMessageCaption.caption(message.caption());
        editMessageCaption.replyMarkup(keyboard);
        return editMessageCaption;
    }

    private EditMessageText buildEditMessageText(Message message, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), message.text());
        editMessageText.replyMarkup(keyboard);
        return editMessageText;
    }

    private RequestData prepareRequestData(boolean startFlag, boolean restartFlag, Long userId, Data data, String nodeId) {
        if (startFlag || restartFlag) {
            if (useWebApp)
                return this.handleWebAppCommandRequest(restartFlag, userId);
            return this.handleNonWebAppCommandRequest(restartFlag, userId, data, nodeId);
        }
        return this.handleDefaultRequest(userId, data, nodeId);
    }

    private void sendRequest(TelegramBot bot, Long userId, RequestData requestData) {
        if (requestData.node.getNodePicture().isBlank()) {
            SendMessage sendMessage = new SendMessage(userId, requestData.node.getNodeText())
                    .replyMarkup(requestData.keyboard);
            bot.execute(sendMessage);
        } else {
            SendPhoto sendPhoto = new SendPhoto(userId, new File(picturesLocation + "/" + requestData.node.getNodePicture()))
                    .caption(requestData.node.getNodeText())
                    .replyMarkup(requestData.keyboard);
            bot.execute(sendPhoto);
        }
    }

    private RequestData handleWebAppCommandRequest(boolean restartFlag, Long userId) {
        if (restartFlag)
            storyInterpreter.restart(userId);

        return new RequestData(
                //TODO поменять текст на property
                new NodeWrapper("", "Let's begin", "", null),
                keyboardFactory.createStartInlineKeyboardMarkupForWebApp(userId)
        );
    }

    private RequestData handleNonWebAppCommandRequest(boolean restartFlag, Long userId, Data data, String nodeId) {
        NodeWrapper node = this.createNodeWrapper(restartFlag, userId, data, nodeId);
        return this.constructRequestData(userId, node);
    }

    private RequestData handleDefaultRequest(Long userId, Data data, String nodeId) {
        NodeWrapper node = storyInterpreter.next(userId, nodeId, data);
        return this.constructRequestData(userId, node);
    }

    private NodeWrapper createNodeWrapper(boolean restartFlag, Long userId, Data data, String nodeId) {
        if (restartFlag)
            return storyInterpreter.restart(userId);
        return storyInterpreter.next(userId, nodeId, data);
    }

    private RequestData constructRequestData(Long userId, NodeWrapper node) {
        Integer version = storyInterpreter.version(userId);
        return new RequestData(
                node,
                keyboardFactory.createInlineKeyboardMarkup(node, version)
        );
    }

    @Builder
    private static final class CallbackData {
        String nodeId;
        String buttonId;
        String nextNodeId;
        Integer version;

        static CallbackData getCallbackData(UserMessage userMessage) {
            if (userMessage.getCallbackData() != null)
                return CallbackData.create(userMessage.getCallbackData());
            return CallbackData.builder().build();
        }

        static CallbackData create(String str) {
            String[] callbackDataArray = str.split(":");
            return CallbackData.builder()
                    .nodeId(callbackDataArray[0])
                    .buttonId(callbackDataArray[1])
                    .nextNodeId(callbackDataArray[2])
                    .version(Integer.valueOf(callbackDataArray[3]))
                    .build();

        }

        @Override
        public int hashCode() {
            return Objects.hash(nodeId, buttonId, nextNodeId, version);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            CallbackData that = (CallbackData) o;
            return Objects.equals(nodeId, that.nodeId)
                    && Objects.equals(buttonId, that.buttonId)
                    && Objects.equals(nextNodeId, that.nextNodeId)
                    && Objects.equals(version, that.version);
        }

        @Override
        public String toString() {
            return nodeId + ":" + buttonId + ":" + nextNodeId + ":" + version;
        }
    }

    private static final class RequestData {
        NodeWrapper node;
        InlineKeyboardMarkup keyboard;

        public RequestData(NodeWrapper node, InlineKeyboardMarkup keyboard) {
            this.node = node;
            this.keyboard = keyboard;
        }
    }

    private final class KeyboardFactory {
        InlineKeyboardMarkup createStartInlineKeyboardMarkupForWebApp(Long userId) {
            String address = this.buildWebAppAddress(userId);
            //TODO поменять текст на property
            InlineKeyboardButton webAppButton = new InlineKeyboardButton("Start").webApp(new WebAppInfo(address));
            return new InlineKeyboardMarkup(webAppButton);
        }

        String buildWebAppAddress(Long userId) {
            return "https://" + domain + "/story?user=" + userId;
        }

        InlineKeyboardMarkup createInlineKeyboardMarkup(NodeWrapper node, Integer version) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            for (Button button : node.getButtons()) {
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton(button.getText());
                keyboardButton.callbackData(this.formCallbackData(button, node, version).toString());
                keyboard.addRow(keyboardButton);
            }
            return keyboard;
        }

        CallbackData formCallbackData(Button button, NodeWrapper node, Integer version) {
            return CallbackData.builder()
                    .nodeId(node.getNodeId())
                    .buttonId(button.getId())
                    .nextNodeId(button.getNextNodeId())
                    .version(version)
                    .build();
        }

        InlineKeyboardMarkup createMarkedInlineKeyboardMarkup(NodeWrapper node, CallbackData callbackData) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            for (Button button : node.getButtons()) {
                InlineKeyboardButton keyboardButton = this.constructMarkedKeyboardButton(button, node, callbackData);
                keyboardButton.callbackData("marked");
                keyboard.addRow(keyboardButton);
            }
            return keyboard;
        }

        InlineKeyboardButton constructMarkedKeyboardButton(Button button, NodeWrapper node, CallbackData callbackData) {
            if (this.formCallbackData(button, node, callbackData.version).equals(callbackData))
                return new InlineKeyboardButton(new String(Character.toChars(0x2705)) + button.getText());
            return new InlineKeyboardButton(button.getText());
        }
    }
}
