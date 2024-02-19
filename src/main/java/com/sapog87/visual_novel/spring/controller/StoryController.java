package com.sapog87.visual_novel.spring.controller;

import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.spring.service.StoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StoryController {

    private final StoryService service;

    public StoryController(StoryService service) {this.service = service;}

    @PostMapping("/export")
    public @ResponseBody Boolean postStory(@RequestBody Root root) {
        return service.saveStoryToFile(root, "stories/story");
    }

    @GetMapping("/import")
    public @ResponseBody Root getStory() {
        return service.loadStoryFromFile("stories/story");
    }
}
