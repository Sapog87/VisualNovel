package com.sapog87.visual_novel.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapog87.visual_novel.core.json.JsonParser;
import com.sapog87.visual_novel.core.json.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoryService {
    public boolean saveStoryToFile(Root root, String path) {
        try {
            JsonParser jsonParser = new JsonParser(path, new ObjectMapper());
            jsonParser.unparse(root);
            log.info("Story was saved to file: {}", path);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Root loadStoryFromFile(String path) {
        try {
            JsonParser jsonParser = new JsonParser(path, new ObjectMapper());
            Root root = jsonParser.parse();
            log.info("Story was loaded from file: {}", path);
            return root;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
