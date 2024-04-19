package com.sapog87.visual_novel.front;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
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
public class BotService {
    private final StoryInterpreter interpreter;
    private final KeyboardFactory keyboardFactory;
    @Value("${domain}")
    private String domain;
    @Value("${story-location}")
    private String storyLocation;
    @Value("${pictures-location}")
    private String picturesLocation;
    @Value("${webapp-mode}")
    private Boolean useWebApp;

    public BotService(StoryInterpreter interpreter) {
        this.interpreter = interpreter;
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
        interpreter.load(storyLocation + fileName);
    }

    @Transactional(dontRollbackOn = UserNotFoundException.class)
    public void handleMessage(TelegramBot bot, UserMessage userMessage) {
        if (userMessage == null)
            return;

        CallbackData callbackData = Helper.getCallbackData(userMessage);

        if (callbackData.version != null && !this.checkVersion(userMessage.getUserId(), callbackData))
            return;

        this.makeButtonsInactive(bot, userMessage.getMessage(), callbackData);
        this.sendChatAction(bot, userMessage.getUserId());
        this.sendMessage(bot, userMessage.isStartFlag(), userMessage.isRestartFlag(), userMessage.getUserId(), callbackData, userMessage.getData());
    }

    private boolean checkVersion(Long userId, CallbackData callbackData) {
        log.info("Checking version: {}", userId);
        return interpreter.version(userId).equals(callbackData.version);
    }

    private void makeButtonsInactive(TelegramBot bot, Message message, CallbackData callbackData) {
        if (callbackData.nodeId == null)
            return;
        MessageData messageData = Helper.getMessageData(message, callbackData.nodeId);
        this.makeButtonsInactive(bot, messageData, callbackData);
    }

    private void sendChatAction(TelegramBot bot, Long userId) {
        ChatAction chatAction = ChatAction.typing;
        SendChatAction sendChatAction = new SendChatAction(userId, chatAction);
        bot.execute(sendChatAction);
    }

    private void sendMessage(TelegramBot bot, boolean startFlag, boolean restartFlag, Long userId, CallbackData callbackData, Data data) {
        log.info("Sending message to user: {}", userId);
        String nodeId = callbackData.nextNodeId;
        RequestData requestData = this.getRequestData(bot, startFlag, restartFlag, userId, data, nodeId);
        this.sendRequest(bot, userId, requestData);
    }

    private void makeButtonsInactive(TelegramBot bot, MessageData message, CallbackData callbackData) {
        log.info("Making buttons inactive");
        if (callbackData.nodeId == null)
            return;
        NodeWrapper node = interpreter.getNode(message.getNodeId());
        EditMessageCaption editMessageCaption = new EditMessageCaption(message.getChatId(), message.getMessageId());
        editMessageCaption.caption(message.getText());
        editMessageCaption.replyMarkup(keyboardFactory.getInactiveInlineKeyboardMarkup(node, callbackData));
        var response = bot.execute(editMessageCaption);
        if (!response.isOk()) {
            EditMessageText editMessageText = new EditMessageText(message.getChatId(), message.getMessageId(), message.getText());
            editMessageText.replyMarkup(keyboardFactory.getInactiveInlineKeyboardMarkup(node, callbackData));
            bot.execute(editMessageText);
        }
    }

    private RequestData getRequestData(TelegramBot bot, boolean startFlag, boolean restartFlag, Long userId, Data data, String nodeId) {
        if (startFlag || restartFlag) {
            if (useWebApp) {
                return this.handleWebAppCommandRequest(restartFlag, userId);
            } else {
                return this.handleNonWebAppCommandRequest(bot, restartFlag, userId, data, nodeId);
            }
        } else {
            return this.handleDefaultRequest(userId, data, nodeId);
        }
    }

    private void sendRequest(TelegramBot bot, Long userId, RequestData requestData) {
        SendResponse response = this.getResponse(bot, userId, requestData);
        if (response.isOk() && !useWebApp) {
            MessageData data = Helper.getMessageData(response.message(), requestData.node.getNodeId());
            interpreter.setLastMessageData(userId, data);
        }
    }

    private RequestData handleWebAppCommandRequest(boolean restartFlag, Long userId) {
        if (restartFlag)
            interpreter.restart(userId);
        //TODO поменять текст на property
        var builder = RequestData.builder();
        builder.node(new NodeWrapper("", "Let's begin", "", null));
        builder.keyboard(keyboardFactory.getStartKeyboardForWebApp(userId));
        return builder.build();
    }

    private RequestData handleNonWebAppCommandRequest(TelegramBot bot, boolean restartFlag, Long userId, Data data, String nodeId) {
        this.handleActiveButtons(bot, userId);
        NodeWrapper node = this.getNodeWrapper(restartFlag, userId, data, nodeId);
        return this.getRequestData(userId, node);
    }

    private RequestData handleDefaultRequest(Long userId, Data data, String nodeId) {
        NodeWrapper node = interpreter.next(userId, nodeId, data);
        return this.getRequestData(userId, node);
    }

    private SendResponse getResponse(TelegramBot bot, Long userId, RequestData requestData) {
        if (requestData.node.getNodePicture().isBlank()) {
            SendMessage sendMessage = new SendMessage(userId, requestData.node.getNodeText()).replyMarkup(requestData.keyboard);
            return bot.execute(sendMessage);
        } else {
            SendPhoto sendPhoto = new SendPhoto(userId, new File(picturesLocation + "/" + requestData.node.getNodePicture())).caption(requestData.node.getNodeText()).replyMarkup(requestData.keyboard);
            return bot.execute(sendPhoto);
        }
    }

    private void handleActiveButtons(TelegramBot bot, Long userId) {
        try {
            MessageData messageData = interpreter.getLastMessageData(userId);
            Integer version = interpreter.version(userId);
            CallbackData localCallbackData = CallbackData.builder().version(version).build();
            this.makeButtonsInactive(bot, messageData, localCallbackData);
        } catch (UserNotFoundException ignored) {
        }
    }

    private NodeWrapper getNodeWrapper(boolean restartFlag, Long userId, Data data, String nodeId) {
        if (restartFlag)
            return interpreter.restart(userId);
        else
            return interpreter.next(userId, nodeId, data);
    }

    private RequestData getRequestData(Long userId, NodeWrapper node) {
        var builder = RequestData.builder();
        builder.node(node);
        Integer version = interpreter.version(userId);
        builder.keyboard(keyboardFactory.getInlineKeyboardMarkup(node, version));
        return builder.build();
    }

    private static final class Helper {
        private static MessageData getMessageData(Message message, String id) {
            String text = Helper.getMessageText(message);
            return MessageData.builder()
                    .messageId(message.messageId())
                    .chatId(message.chat().id())
                    .text(text)
                    .nodeId(id)
                    .build();
        }

        private static String getMessageText(Message message) {
            if (Objects.nonNull(message.text())) {
                return message.text();
            } else {
                return message.caption();
            }
        }

        private static CallbackData getCallbackData(UserMessage userMessage) {
            if (userMessage.getCallbackData() != null)
                return CallbackData.create(userMessage.getCallbackData());
            return CallbackData.builder().build();
        }
    }

    @Builder
    private static class CallbackData {
        String nodeId;
        String buttonId;
        String nextNodeId;
        Integer version;

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

    @Builder
    private static class RequestData {
        NodeWrapper node;
        InlineKeyboardMarkup keyboard;
    }

    private final class KeyboardFactory {
        InlineKeyboardMarkup getStartKeyboardForWebApp(Long userId) {
            String address = this.buildWebAppAddress(userId);
            //TODO поменять текст на property
            InlineKeyboardButton webAppButton = new InlineKeyboardButton("Start").webApp(new WebAppInfo(address));
            return new InlineKeyboardMarkup(webAppButton);
        }

        String buildWebAppAddress(Long userId) {
            return "https://" + domain + "/story?user=" + userId;
        }

        InlineKeyboardMarkup getInlineKeyboardMarkup(NodeWrapper node, Integer version) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            for (Button button : node.getButtons()) {
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton(button.getText());
                keyboardButton.callbackData(this.buildCallbackData(button, node, version).toString());
                keyboard.addRow(keyboardButton);
            }
            return keyboard;
        }

        CallbackData buildCallbackData(Button button, NodeWrapper node, Integer version) {
            return CallbackData.builder()
                    .nodeId(node.getNodeId())
                    .buttonId(button.getId())
                    .nextNodeId(button.getNextNodeId())
                    .version(version)
                    .build();
        }

        InlineKeyboardMarkup getInactiveInlineKeyboardMarkup(NodeWrapper node, CallbackData callbackData) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            for (Button button : node.getButtons()) {
                InlineKeyboardButton keyboardButton = this.getKeyboardButton(button, node, callbackData);
                keyboardButton.callbackData("inactive");
                keyboard.addRow(keyboardButton);
            }
            return keyboard;
        }

        InlineKeyboardButton getKeyboardButton(Button button, NodeWrapper node, CallbackData callbackData) {
            if (this.buildCallbackData(button, node, callbackData.version).equals(callbackData))
                return new InlineKeyboardButton(new String(Character.toChars(0x2705)) + button.getText());
            return new InlineKeyboardButton(button.getText());
        }
    }
}
