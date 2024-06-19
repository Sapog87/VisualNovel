package com.sapog87.visual_novel.front;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.sapog87.visual_novel.app.service.TelegramService;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.front.adapter.UserMessage;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.data.UserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
public final class Bot {
    private final TelegramBot bot;
    private final TelegramService telegramService;

    public Bot(TelegramBot bot, TelegramService telegramService) {
        this.bot = bot;
        this.telegramService = telegramService;
    }

    public void start(String fileName, Function<Root, Story> storyFunction) {
        telegramService.start(fileName, storyFunction);
        this.botInit(bot);
        log.info("Bot was turned on");
    }

    private void botInit(TelegramBot bot) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                UserMessage userMessage = this.getUserMessage(update);
                try {
                    telegramService.handleMessage(bot, userMessage);
                } catch (Exception ignored) {
                    try {
                        bot.execute(new SendMessage(update.message().chat().id(), "Что-то пошло не так"));
                    } catch (Exception e) {
                        bot.execute(new SendMessage(update.callbackQuery().message().chat().id(), "Что-то пошло не так"));
                    }
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

    private UserMessage getUserMessage(Update update) {
        if (Objects.nonNull(update.message())) {
            return this.handleMessage(update);
        } else if (Objects.nonNull(update.callbackQuery()) && !update.callbackQuery().data().equals("marked")) {
            return this.handleCallback(update);
        } else {
            return null;
        }
    }

    private UserMessage handleMessage(Update update) {
        if (update.message().text() == null)
            return null;
        var builder = UserMessage.builder();
        Long userId = update.message().from().id();
        builder.userId(userId);

        if (update.message().text().equals("/start")) {
            builder.startFlag(true);
            log.info("Starting or continuing story for user: {}", userId);
        } else if (update.message().text().equals("/restart")) {
            builder.restartFlag(true);
            log.info("Restarting story for user: {}", userId);
        } else {
            Data data = new UserData(userId, null, update.message().text());
            builder.data(data);
        }

        return builder.build();
    }

    private UserMessage handleCallback(Update update) {
        var builder = UserMessage.builder();

        Long userId = update.callbackQuery().from().id();
        builder.userId(userId);
        String callbackData = update.callbackQuery().data();
        builder.callbackData(callbackData);
        Message message = update.callbackQuery().message();
        builder.message(message);

        return builder.build();
    }

    public void stop() {
        bot.shutdown();
        log.info("Bot was shut down");
    }

}
