package com.sapog87.visual_novel.front.telegram;

import lombok.Getter;

@Getter
public class Button {
    private String id;
    private String text;
    private String nextNodeId;

    public Button(String id, String text, String nextNodeId) {
        this.id = id;
        this.text = text;
        this.nextNodeId = nextNodeId;
    }
}
