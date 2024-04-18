package com.sapog87.visual_novel.front.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.front.AbstractStoryInterpreterConsumer;
import com.sapog87.visual_novel.interpreter.StoryInterpreter;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.data.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

@Slf4j
@Component
public class Bot extends AbstractStoryInterpreterConsumer {
    private final TelegramBot bot;
    @Value("${domain}")
    private String domain;
    @Value("${story-location}")
    private String storyLocation;
    @Value("${pictures-location}")
    private String picturesLocation;
    @Value("${webapp-mode}")
    private Boolean useWebApp;

    public Bot(StoryInterpreter interpreter, TelegramBot bot) {
        super(interpreter);
        this.bot = bot;
    }

    private String getMessageText(Message message) {
        if (Objects.nonNull(message.text())) {
            return message.text();
        } else {
            return message.caption();
        }
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
        botInit(bot);
    }

    public void stop() {
        bot.shutdown();
        log.info("Bot was shut down");
    }

    private void botInit(TelegramBot bot) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                //TODO добавть возврат сообщения с предложением начать сначала в случае необработаного исключения
                Long userId;
                String nodeId = null;
                boolean startFlag = false;
                boolean restartFlag = false;
                Data data = null;

                if (Objects.nonNull(update.message())) {
                    userId = update.message().from().id();

                    if (update.message().text().equals("/start")) {
                        log.info("Starting or continuing story for user: {}", userId);
                        startFlag = true;
                    } else if (update.message().text().equals("/restart")) {
                        log.info("Restarting story for user: {}", userId);
                        restartFlag = true;
                    } else {
                        data = new UserData(userId, null, update.message().text());
                    }
                } else if (Objects.nonNull(update.callbackQuery()) && !update.callbackQuery().data().equals("inactive")) {
                    userId = update.callbackQuery().from().id();
                    String callbackData = update.callbackQuery().data();
                    String[] callbackDataArray = callbackData.split(":");
                    String prevNodeId = callbackDataArray[0];
                    nodeId = callbackDataArray[2];
                    Integer version = Integer.valueOf(callbackDataArray[3]);
                    Message message = update.callbackQuery().message();

                    if (!interpreter.version(userId).equals(version))
                        return;

                    makeButtonsInactive(bot, getMessageData(message, prevNodeId), callbackData, version);
                } else {
                    return;
                }

                sendChatAction(bot, userId);
                sendMessage(bot, startFlag, restartFlag, userId, nodeId, data);
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                e.response().errorCode();
                e.response().description();
            } else {
                log.error(e.getMessage());
            }
        });
    }

    private void sendChatAction(TelegramBot bot, Long userId) {
        ChatAction chatAction = ChatAction.typing;
        SendChatAction sendChatAction = new SendChatAction(userId, chatAction);
        bot.execute(sendChatAction);
    }

    private void makeButtonsInactive(TelegramBot bot, MessageData data, String callbackData, Integer version) {
        NodeWrapper node = interpreter.getNode(data.getNodeId());
        EditMessageCaption editMessageCaption = new EditMessageCaption(data.getChatId(), data.getMessageId());
        editMessageCaption.caption(data.getText());
        editMessageCaption.replyMarkup(getInactiveInlineKeyboardMarkup(node, callbackData, version));
        var response = bot.execute(editMessageCaption);
        if (!response.isOk()) {
            EditMessageText editMessageText = new EditMessageText(data.getChatId(), data.getMessageId(), data.getText());
            editMessageText.replyMarkup(getInactiveInlineKeyboardMarkup(node, callbackData, version));
            bot.execute(editMessageText);
        }
    }

    private MessageData getMessageData(Message message, String nodeId) {
        String text = getMessageText(message);
        return MessageData.builder()
                .messageId(message.messageId())
                .chatId(message.chat().id())
                .text(text)
                .nodeId(nodeId)
                .build();
    }

    private void sendMessage(TelegramBot bot, boolean startFlag, boolean restartFlag, Long userId, String nodeId, Data data) {
        NodeWrapper node;
        InlineKeyboardMarkup keyboard;

        if (startFlag || restartFlag) {
            if (useWebApp) {
                if (restartFlag)
                    interpreter.restart(userId);
                //TODO поменять текст на property
                node = new NodeWrapper("", "Let's begin", "", null);
                keyboard = buildStartKeyboardForWebApp(userId);
            } else {
                try {
                    MessageData messageData = interpreter.getLastMessageData(userId);
                    Integer version = interpreter.version(userId);
                    makeButtonsInactive(bot, messageData, "", version);
                } catch (UserNotFoundException ignored) {
                }
                if (restartFlag)
                    node = interpreter.restart(userId);
                else
                    node = interpreter.next(userId, nodeId, data);
                Integer version = interpreter.version(userId);
                keyboard = getInlineKeyboardMarkup(node, version);
                nodeId = node.getNodeId();
            }
        } else {
            node = interpreter.next(userId, nodeId, data);
            Integer version = interpreter.version(userId);
            keyboard = getInlineKeyboardMarkup(node, version);
            nodeId = node.getNodeId();
        }

        log.info("Sending message to user: {}", userId);
        sendNodeMessage(bot, userId, node, keyboard, nodeId);
    }

    private InlineKeyboardMarkup getInactiveInlineKeyboardMarkup(NodeWrapper node, String callbackData, Integer version) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Button button : node.getButtons()) {
            InlineKeyboardButton keyboardButton = getKeyboardButton(button, node, callbackData, version);
            keyboardButton.callbackData("inactive");
            keyboard.addRow(keyboardButton);
        }
        return keyboard;
    }

    private InlineKeyboardButton getKeyboardButton(Button button, NodeWrapper node, String callbackData, Integer version) {
        if (buildCallbackData(button, node, version).equals(callbackData))
            return new InlineKeyboardButton(new String(Character.toChars(0x2705)) + button.getText());
        return new InlineKeyboardButton(button.getText());
    }

    private String buildCallbackData(Button button, NodeWrapper node, Integer version) {
        return node.getNodeId() + ":" + button.getId() + ":" + button.getNextNodeId() + ":" + version;
    }

    private InlineKeyboardMarkup buildStartKeyboardForWebApp(Long userId) {
        String address = buildWebAppAddress(userId);
        //TODO поменять текст на property
        InlineKeyboardButton webAppButton = new InlineKeyboardButton("Start").webApp(new WebAppInfo(address));
        return new InlineKeyboardMarkup(webAppButton);
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(NodeWrapper node, Integer version) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Button button : node.getButtons()) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton(button.getText());
            keyboardButton.callbackData(buildCallbackData(button, node, version));
            keyboard.addRow(keyboardButton);
        }
        return keyboard;
    }

    private void sendNodeMessage(TelegramBot bot, Long userId, NodeWrapper node, InlineKeyboardMarkup keyboard, String prevNodeId) {
        SendResponse response;
        if (node.getNodePicture().isBlank()) {
            SendMessage sendMessage = new SendMessage(userId, node.getNodeText()).replyMarkup(keyboard);
            response = bot.execute(sendMessage);
        } else {
            SendPhoto sendPhoto = new SendPhoto(userId, new File(picturesLocation + "/" + node.getNodePicture())).caption(node.getNodeText()).replyMarkup(keyboard);
            response = bot.execute(sendPhoto);
        }
        if (response.isOk() && !useWebApp) {
            MessageData data = getMessageData(response.message(), prevNodeId);
            interpreter.setLastMessageData(userId, data);
        }
    }

    private String buildWebAppAddress(Long userId) {
        return "https://" + domain + "/story?user=" + userId;
    }
}
