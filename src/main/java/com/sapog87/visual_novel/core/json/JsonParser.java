package com.sapog87.visual_novel.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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
            return objectMapper.readValue(fileReader, Root.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unparse(Root root) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            objectMapper.writeValue(fileWriter, root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}