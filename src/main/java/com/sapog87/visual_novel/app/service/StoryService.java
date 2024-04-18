package com.sapog87.visual_novel.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapog87.visual_novel.core.json.JsonParser;
import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.core.story.Story;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class StoryService {
    private final ObjectMapper objectMapper;

    public StoryService(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    public boolean saveStoryToFile(Root root, String path) {
        try {
            new JsonParser(path + ".temp", objectMapper).unparse(root);
            log.info("Story was saved to file: {}", path + ".temp");
            validateStory(root);
            Path story = Path.of(path);
            Files.deleteIfExists(story);
            Files.copy(Path.of(path + ".temp"), story);
            log.info("Story was saved to file: {}", path);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateStory(Root root) {
        new Story(root);
    }

    public Root loadStoryFromFile(String path) {
        try {
            JsonParser jsonParser = new JsonParser(path, objectMapper);
            Root root = jsonParser.parse();
            log.info("Story was loaded from file: {}", path);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
