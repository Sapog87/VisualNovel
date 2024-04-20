package com.sapog87.visual_novel.front.adapter;

import lombok.Getter;

@Getter
public class Button {
    private final String id;
    private final String text;
    private final String nextNodeId;

    public Button(String id, String text, String nextNodeId) {
        this.id = id;
        this.text = text;
        this.nextNodeId = nextNodeId;
    }
}
