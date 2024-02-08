package com.sapog87.visual_novel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.sapog87.visual_novel.core.json.JsonParser;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.parser.Parser;
import com.sapog87.visual_novel.core.story.Story;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

@SpringBootApplication
public class VisualNovelApplication {

    @Bean
    public TelegramBot telegramBot(@Value("${BOT_TOKEN}") String token) {
        return new TelegramBot(token);
    }

    public static void main(String[] args) {
        File file = new File("stories/story.json");
        JsonParser jsonParser = new JsonParser(file, new ObjectMapper());
        Root root = jsonParser.parse();
        Story story = new Story(root);

        /*
        var context = SpringApplication.run(VisualNovelApplication.class, args);
        TelegramBot bot = (TelegramBot) context.getBean("telegramBot");
        System.out.println(bot.getToken());

        bot.setUpdatesListener(updates -> {

            updates.forEach(x -> {
                Long id = x.message().from().id();
                System.out.println(id);

                InlineKeyboardButton keyboardButton = new InlineKeyboardButton("fffrr");
                keyboardButton.webApp(new WebAppInfo("https://www.youtube.com"));
                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(keyboardButton);

                Keyboard replyKeyboardRemove = new ReplyKeyboardRemove();
                SendMessage request = new SendMessage(id, "let's start")
                        .replyMarkup(keyboard);

                bot.execute(request);
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                e.response().errorCode();
                e.response().description();
            } else {
                e.printStackTrace();
            }
        });
        */
    }

}
