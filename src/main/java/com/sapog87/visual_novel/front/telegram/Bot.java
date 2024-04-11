package com.sapog87.visual_novel.front.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import com.sapog87.visual_novel.app.exception.UserNotFoundException;
import com.sapog87.visual_novel.front.AbstractStoryInterpreterConsumer;
import com.sapog87.visual_novel.interpreter.StoryInterpreter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    public void startStory() {
        log.info("Web app mode: {}", useWebApp);
        if (useWebApp) {
            if (domain == null || domain.isBlank())
                throw new IllegalStateException("Domain is blank");
            log.info("Domain: {}", domain);
        }
        log.info("Story location: {}", storyLocation);
        log.info("Pictures-location: {}", picturesLocation);
        interpreter.load(storyLocation + "/story.json");
        botInit(bot);
    }

    private void botInit(TelegramBot bot) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                Long userId;
                String nodeId;
                boolean startFlag = false;
                boolean restartFlag = false;
                String text = null;

                if (Objects.nonNull(update.message())) {
                    userId = update.message().from().id();
                    nodeId = null;

                    if (update.message().text().equals("/start")) {
                        log.info("Starting or continuing story for user: {}", userId);
                        startFlag = true;
                    } else if (update.message().text().equals("/restart")) {
                        log.info("Restarting story for user: {}", userId);
                        restartFlag = true;
                    } else {
                        text = update.message().text();
                    }
                } else if (Objects.nonNull(update.callbackQuery()) && !update.callbackQuery().data().equals("inactive")) {
                    userId = update.callbackQuery().from().id();
                    nodeId = update.callbackQuery().data();
                    Message message = update.callbackQuery().message();

                    makeButtonsInactive(bot, userId, getMessageData(message));
                } else {
                    return;
                }

                sendMessage(bot, startFlag, restartFlag, userId, nodeId, text);
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

    private void makeButtonsInactive(TelegramBot bot, Long userId, MessageData data) {
        var node = interpreter.current(userId);
        EditMessageCaption editMessageCaption = new EditMessageCaption(data.getChatId(), data.getMessageId());
        editMessageCaption.caption(node.getNodeText());
        editMessageCaption.replyMarkup(getInactiveInlineKeyboardMarkup(node));
        var response = bot.execute(editMessageCaption);
        if (!response.isOk()) {
            EditMessageText editMessageText = new EditMessageText(data.getChatId(), data.getMessageId(), node.getNodeText());
            editMessageText.replyMarkup(getInactiveInlineKeyboardMarkup(node));
            bot.execute(editMessageText);
        }
    }

    private MessageData getMessageData(Message message) {
        MessageData data = new MessageData();
        data.setMessageId(message.messageId());
        data.setChatId(message.chat().id());
        return data;
    }

    private void sendMessage(TelegramBot bot, boolean startFlag, boolean restartFlag, Long userId, String nodeId, String text) {
        StoryNodeWrapper node;
        InlineKeyboardMarkup keyboard;
        if (startFlag) {
            if (useWebApp) {
                //TODO поменять текст на property
                node = new StoryNodeWrapper("Let's begin", "", null);
                keyboard = buildStartKeyboardForWebApp(userId);
            } else {
                try {
                    var data = interpreter.getLastMessageData(userId);
                    makeButtonsInactive(bot, userId, data);
                } catch (UserNotFoundException ignored) {
                }
                node = interpreter.next(userId, nodeId, text);
                keyboard = getInlineKeyboardMarkup(node);
            }
        } else if (restartFlag) {
            node = interpreter.restart(userId);
            if (useWebApp) {
                //TODO поменять текст на property
                node = new StoryNodeWrapper("Let's begin", "", null);
                keyboard = buildStartKeyboardForWebApp(userId);
            } else {
                keyboard = getInlineKeyboardMarkup(node);
            }
        } else {
            node = interpreter.next(userId, nodeId, text);
            keyboard = getInlineKeyboardMarkup(node);
        }
        log.info("Sending message to user: {}", userId);
        sendNodeMessage(bot, userId, node, keyboard);
    }

    @NotNull
    private InlineKeyboardMarkup getInactiveInlineKeyboardMarkup(StoryNodeWrapper node) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Button button : node.getButtons()) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton(button.getText());
            keyboardButton.callbackData("inactive");
            keyboard.addRow(keyboardButton);
        }
        return keyboard;
    }

    private InlineKeyboardMarkup buildStartKeyboardForWebApp(Long userId) {
        String address = buildWebAppAddress(userId);
        //TODO поменять текст на property
        InlineKeyboardButton webAppButton = new InlineKeyboardButton("Start").webApp(new WebAppInfo(address));
        return new InlineKeyboardMarkup(webAppButton);
    }

    @NotNull
    private InlineKeyboardMarkup getInlineKeyboardMarkup(StoryNodeWrapper node) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        for (Button button : node.getButtons()) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton(button.getText());
            keyboardButton.callbackData(button.getNextNodeId());
            keyboard.addRow(keyboardButton);
        }
        return keyboard;
    }

    private void sendNodeMessage(TelegramBot bot, Long userId, StoryNodeWrapper node, InlineKeyboardMarkup keyboard) {
        SendResponse response;
        if (node.getNodePicture().isBlank()) {
            SendMessage sendMessage = new SendMessage(userId, node.getNodeText()).replyMarkup(keyboard);
            response = bot.execute(sendMessage);
        } else {
            SendPhoto sendPhoto = new SendPhoto(userId, new File(picturesLocation + "/" + node.getNodePicture())).caption(node.getNodeText()).replyMarkup(keyboard);
            response = bot.execute(sendPhoto);
        }
        MessageData data = getMessageData(response.message());
        interpreter.setLastMessageData(userId, data);
    }

    private String buildWebAppAddress(Long userId) {
        return "https://" + domain + "/story?user=" + userId;
    }
}
