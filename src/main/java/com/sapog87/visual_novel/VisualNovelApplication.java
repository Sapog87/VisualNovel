package com.sapog87.visual_novel;

import com.pengrad.telegrambot.TelegramBot;
import com.sapog87.visual_novel.front.telegram.Bot;
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
        var context = SpringApplication.run(VisualNovelApplication.class, args);
        context.getBean(Bot.class).start("story.json");

        //TODO добавить security
        //TODO добавить интерполяцию строк
        //TODO переделать валидации

        //TODO дописать отладчик
        //TODO довавить возможность выделять и перемещать сразу несколько узлов
        //TODO пофиксить срывы при выходе за пределы окна
        //TODO сохранять ширину узла в зависимости от textarea

    }

}
