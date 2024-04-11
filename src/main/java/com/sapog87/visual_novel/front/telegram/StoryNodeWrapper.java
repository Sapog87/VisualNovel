package com.sapog87.visual_novel.front.telegram;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoryNodeWrapper {
    private String nodeText;
    private String nodePicture;
    private List<Button> buttons;

    public StoryNodeWrapper(String nodeText, String nodePicture, List<Button> buttons) {
        this.nodeText = nodeText;
        this.nodePicture = nodePicture;
        this.buttons = buttons;
    }
}
