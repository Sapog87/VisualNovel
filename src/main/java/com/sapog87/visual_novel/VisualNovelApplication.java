package com.sapog87.visual_novel;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VisualNovelApplication {

    @Bean
    public TelegramBot telegramBot(@Value("${BOT_TOKEN}") String token) {
        return new TelegramBot(token);
    }

    public static void main(String[] args) {
        SpringApplication.run(VisualNovelApplication.class, args);

        /*
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
