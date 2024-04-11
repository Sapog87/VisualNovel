package com.sapog87.visual_novel.app.controller;

import com.sapog87.visual_novel.interpreter.StoryInterpreter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
public class WebAppController {
    private final StoryInterpreter interpreter;
    @Value("${template-name}")
    private String templateName;
    @Value("${webapp-mode}")
    private Boolean useWebApp;

    public WebAppController(StoryInterpreter interpreter) {this.interpreter = interpreter;}

    @GetMapping("/story")
    public String nextNode(Model model, @RequestParam("user") String user, @RequestParam(value = "node", required = false) String node) {
        if (!useWebApp) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        //TODO добавить возможность слать сообщение
        //TODO добавить возможность слать запросы с разных устройств одновременно без нарушения повествования или ограничить доступ с нескольких устройств
        Long userId = Long.parseLong(user);
        var data = interpreter.next(userId, node, null);
        model.addAttribute("data", data);
        model.addAttribute("userId", userId);
        return templateName;
    }

    //TODO добавить рестарт
}
