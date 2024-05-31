package com.sapog87.visual_novel.app.controller;

import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.app.service.TokenService;
import com.sapog87.visual_novel.interpreter.StoryInterpreter;
import com.sapog87.visual_novel.interpreter.data.UserData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
public class WebAppController {
    private final StoryInterpreter storyInterpreter;
    private final TokenService tokenService;
    @Value("${template-name}")
    private String templateName;
    @Value("${webapp-mode}")
    private Boolean useWebApp;

    public WebAppController(StoryInterpreter storyInterpreter, TokenService tokenService) {
        this.storyInterpreter = storyInterpreter;
        this.tokenService = tokenService;
    }

    @GetMapping("/story")
    public String nextNode(Model model, @RequestParam("user") String user, @RequestParam(value = "node", required = false) String node, @RequestParam(value = "data", required = false) String data, HttpServletRequest request) {
        if (!useWebApp || !request.getUserPrincipal().getName().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        //TODO прикрутить версионирование
        Long userId = Long.parseLong(user);
        UserData userData = null;
        if (data != null)
            userData = new UserData(userId, node, data);
        var modelData = storyInterpreter.next(userId, node, userData);

        model.addAttribute("picture", modelData.getNodePicture());
        model.addAttribute("text", modelData.getNodeText());
        model.addAttribute("buttons", modelData.getButtons());
        model.addAttribute("userId", userId);

        model.addAttribute("textField", modelData.getHasTextField());
        model.addAttribute("nodeId", modelData.getNodeId());

        return templateName;
    }

    @GetMapping("/auth")
    public String verifyLink(@RequestParam String token) {
        User user = tokenService.getUserFromToken(token);
        return "redirect:/story?user=" + user.getExternalUserId();
    }
}
