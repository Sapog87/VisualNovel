package com.sapog87.visual_novel.app.controller;

import com.sapog87.visual_novel.app.service.StoryService;
import com.sapog87.visual_novel.core.json.Root;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StoryController {

    @Value("${story-location}")
    private String storyLocation;

    private final StoryService service;

    public StoryController(StoryService service) {this.service = service;}

    @PostMapping("/export")
    public @ResponseBody Boolean postStory(@RequestBody Root root) {
        return service.saveStoryToFile(root, storyLocation + "/story.json");
    }

    @GetMapping("/import")
    public @ResponseBody Root getStory() {
        return service.loadStoryFromFile(storyLocation + "/story.json");
    }

}
