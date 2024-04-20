package com.sapog87.visual_novel.app.controller;

import com.sapog87.visual_novel.app.service.StoryService;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.interpreter.data.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StoryController {

    private final StoryService service;
    @Value("${story-location}")
    private String storyLocation;

    public StoryController(StoryService service) {this.service = service;}

    @PostMapping("/export")
    public @ResponseBody Boolean postStory(@RequestBody Root root) {
        return service.saveStoryToFile(root, storyLocation + "story.json");
    }

    @GetMapping("/import")
    public @ResponseBody Root getStory() {
        return service.loadStoryFromFile(storyLocation + "story.json.temp");
    }

    int k = 0;

    @PostMapping("/api")
    public @ResponseBody Response api() {
        Response response = new Response();
        response.setAnswer(String.valueOf(++k));
        response.setUserId(1L);
        response.setStop(k==2);
        return response;
    }
}
