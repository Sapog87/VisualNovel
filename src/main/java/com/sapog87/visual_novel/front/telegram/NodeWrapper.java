package com.sapog87.visual_novel.front.telegram;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NodeWrapper {
    private String nodeId;
    private String nodeText;
    private String nodePicture;
    private List<Button> buttons;
    private Boolean hasTextField;

    public NodeWrapper(String nodeId, String nodeText, String nodePicture, List<Button> buttons) {
        this.nodeId = nodeId;
        this.nodeText = nodeText;
        this.nodePicture = nodePicture;
        this.buttons = buttons;
    }
}
