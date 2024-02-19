package com.sapog87.visual_novel.telegram;

import lombok.Getter;

import java.util.List;

@Getter
public class StoryNodeWrapperForTelegramBot {
    private String nodeText;
    private String nodePicture;
    private List<Button> buttons;

    public StoryNodeWrapperForTelegramBot(String nodeText, String nodePicture, List<Button> buttons) {
        this.nodeText = nodeText;
        this.nodePicture = nodePicture;
        this.buttons = buttons;
    }
}
