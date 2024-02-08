package com.sapog87.visual_novel.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;

public class JsonParser {

    private final Logger logger = LogManager.getLogger(JsonParser.class);
    private final File file;
    private final ObjectMapper objectMapper;

    public JsonParser(File file, ObjectMapper objectMapper) {
        this.file = file;
        this.objectMapper = objectMapper;
    }

    public JsonParser(String filePath, ObjectMapper objectMapper) {
        this(new File(filePath), objectMapper);
    }

    public Root parse() {
        try (FileReader fileReader = new FileReader(file)) {
            Root root = objectMapper.readValue(fileReader, Root.class);

            logger.info("Successful parsing");
            return root;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
