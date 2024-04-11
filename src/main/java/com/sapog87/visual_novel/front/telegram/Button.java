package com.sapog87.visual_novel.front.telegram;

import lombok.Getter;

@Getter
public class Button {
    private String text;
    private String nextNodeId;

    public Button(String text, String nextNodeId) {
        this.text = text;
        this.nextNodeId = nextNodeId;
    }
}
