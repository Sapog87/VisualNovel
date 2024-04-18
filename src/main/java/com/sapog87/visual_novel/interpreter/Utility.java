package com.sapog87.visual_novel.interpreter;

import com.sapog87.visual_novel.core.story.Story;
import com.sapog87.visual_novel.core.story.nodes.ButtonStoryNode;
import com.sapog87.visual_novel.core.story.nodes.StoryNode;
import com.sapog87.visual_novel.core.story.nodes.TerminalStoryNode;
import com.sapog87.visual_novel.front.telegram.Button;
import com.sapog87.visual_novel.front.telegram.NodeWrapper;

import java.util.List;

public class Utility {

    private Utility() {}

    public static NodeWrapper getStoryNodeWrapper(StoryNode node, Story story) {
        List<Button> buttons = Utility.getButtons(node, story);

        return new NodeWrapper(
                node.getId(),
                node.getData().get("text"),
                node.getData().get("picture"),
                buttons
        );
    }

    public static List<Button> getButtons(StoryNode node, Story story) {
        if (!(node instanceof TerminalStoryNode))
            throw new IllegalArgumentException("Terminal story node expected {" + node.getId() + "}");
        if (node.getNext().isEmpty())
            return List.of();
        return node.getNext().get(0)
                .stream().map(id -> {
                    var storyButton = ((ButtonStoryNode) story.getNodeById(id));
                    return new Button(
                            storyButton.getId(),
                            storyButton.getData().get("text"),
                            storyButton.getNext().get(0).get(0)
                    );
                }).toList();
    }
}
