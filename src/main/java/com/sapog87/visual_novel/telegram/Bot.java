package com.sapog87.visual_novel.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.sapog87.visual_novel.interpreter.StoryInterpreterForTelegram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;

@Slf4j
@Component
public class Bot {
    private final StoryInterpreterForTelegram interpreter;
    private final TelegramBot bot;

    public Bot(StoryInterpreterForTelegram interpreter, TelegramBot bot) {
        this.interpreter = interpreter;
        this.bot = bot;

        startStory("stories/story");
    }

    public void startStory(String path) {
        interpreter.load(path);
        botInit(bot);
    }

    //TODO keyboardButton.webApp(new WebAppInfo("https://www.youtube.com"));
    //TODO webhook + spring
    private void botInit(TelegramBot bot) {
        bot.setUpdatesListener(updates -> {
            //TODO
            updates.forEach(update -> {
                Long userId;
                String nodeId;
                if (Objects.nonNull(update.message())) {
                    userId = update.message().from().id();
                    nodeId = null;
                } else {
                    userId = update.callbackQuery().from().id();
                    nodeId = update.callbackQuery().data();
                }
                log.info("Telegram user id:" + userId);
                StoryNodeWrapperForTelegramBot node = interpreter.next(userId, nodeId);

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                for (Button button : node.getButtons()) {
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton(button.getText());
                    keyboardButton.callbackData(button.getNextNodeId());
                    keyboard.addRow(keyboardButton);
                }

                if (node.getNodePicture().isBlank()) {
                    SendMessage request = new SendMessage(userId, node.getNodeText()).replyMarkup(keyboard);
                    bot.execute(request);
                } else {
                    SendPhoto sendPhoto = new SendPhoto(userId, new File(node.getNodePicture())).caption(node.getNodeText()).replyMarkup(keyboard);
                    bot.execute(sendPhoto);
                }

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
}
