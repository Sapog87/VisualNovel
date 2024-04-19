package com.sapog87.visual_novel.interpreter.handler.terminal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapog87.visual_novel.app.entity.User;
import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.APIStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.front.Button;
import com.sapog87.visual_novel.front.NodeWrapper;
import com.sapog87.visual_novel.interpreter.Response;
import com.sapog87.visual_novel.interpreter.Utility;
import com.sapog87.visual_novel.interpreter.data.Data;
import com.sapog87.visual_novel.interpreter.handler.StoryNodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class APIStoryNodeHandler implements StoryNodeHandler<NodeWrapper> {
    private final ObjectMapper objectMapper;

    public APIStoryNodeHandler(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    @Override
    public NodeWrapper handle(StoryNode node, User user, Story story, Data data) {
        String url = node.getData().get("url");
        return getResponse(data, url, story, node, user);
    }

    private NodeWrapper getResponse(Data data, String url, Story story, StoryNode node, User user) {
        NodeWrapper nodeWrapper;
        try {
            String json = objectMapper.writeValueAsString(data);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    //TODO поменять timeout на property
                    .timeout(Duration.ofSeconds(5))
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                Response r = objectMapper.readValue(response.body(), Response.class);
                List<Button> buttons = List.of();
                boolean field = true;
                if (r.getStop()) {
                    field = false;
                    buttons = Utility.getButtons(node, story);
                }

                nodeWrapper = new NodeWrapper(node.getId(), r.getAnswer(), "", buttons);
                nodeWrapper.setHasTextField(field);
            } else {
                //TODO поменять текст на property
                nodeWrapper = new NodeWrapper(node.getId(),"Ошибка при получении сообщения", "", Utility.getButtons(node, story));
                nodeWrapper.setHasTextField(false);
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error occurred during sending data. url: {}, userId: {}, nodeId: {}, data {}, error: {}", url, user.getId(), user.getStoryNodeId(), data, e.toString());
            //TODO поменять текст на property
            nodeWrapper = new NodeWrapper(node.getId(),"Ошибка при отправке сообщения", "", Utility.getButtons(node, story));
            nodeWrapper.setHasTextField(false);
        }

        return nodeWrapper;
    }

    @Override
    public Class<? extends StoryNode> getHandledClass() {
        return APIStoryNode.class;
    }
}
