package com.sapog87.visual_novel.spring.service;

import com.sapog87.visual_novel.core.json.Root;
import com.sapog87.visual_novel.spring.repository.StoryUserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Transactional
public class StoryService {
    private final Logger logger = LoggerFactory.getLogger(StoryService.class);
    private final StoryUserRepository repository;

    public StoryService(StoryUserRepository repository) {this.repository = repository;}

    public boolean saveStoryToFile(Root root, String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(root);
            out.flush();
            logger.info("Story was saved to file");
            return true;
        } catch (IOException e) {
            logger.error("Story wasn't saved to file", e);
            return false;
        }
    }

    public Root loadStoryFromFile(String path) {
        try (ObjectInputStream out = new ObjectInputStream(new FileInputStream(path))) {
            Root root = (Root) out.readObject();
            logger.info("Story was loaded from file");
            return root;
        } catch (IOException e) {
            logger.error("Story wasn't saved to file", e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
